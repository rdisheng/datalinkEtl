@echo off
if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

set CUSTOM_SEARCH_LOCATIONS=file:%BASE_DIR%/conf/

set MODE="standalone"
set SERVER=datalink-server
set MODE_INDEX=-1
set SERVER_INDEX=-1


set i=0
for %%a in (%*) do (
    if "%%a" == "-m" ( set /a MODE_INDEX=!i!+1 )
    if "%%a" == "-s" ( set /a SERVER_INDEX=!i!+1 )
    set /a i+=1
)

set i=0
for %%a in (%*) do (
    if %MODE_INDEX% == !i! ( set MODE="%%a" )
    if %SERVER_INDEX% == !i! (set SERVER="%%a")
    set /a i+=1
)

rem if datalink startup mode is standalone
if %MODE% == "standalone" (
    echo "datalink is starting with standalone"
    set "DATALINK_JVM_OPTS=-Xms512m -Xmx512m -Xmn256m"
)

rem if datalink startup mode is cluster
if %MODE% == "cluster" (
    echo "datalink is starting with cluster"
    set "DATALINK_OPTS=-Ddatalink.cluster=true"
    set "DATALINK_JVM_OPTS=-server -Xms2g -Xmx2g -Xmn1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%BASE_DIR%\logs\java_heapdump.hprof -XX:-UseLargePages"
)

rem if datalink startup mode is standalone
echo "datalink is starting"
set "DATALINK_JVM_OPTS=-Xms512m -Xmx512m -Xmn256m"

rem set datalink options
set "DATALINK_OPTS=%DATALINK_OPTS% -Ddatalink.home=%BASE_DIR% -Duser.timezone=GMT+08"
set "DATALINK_OPTS=%DATALINK_OPTS% -jar %BASE_DIR%\target\%SERVER%.jar"

rem set datalink spring config location
set "DATALINK_CONFIG_OPTS=--spring.config.additional-location=%CUSTOM_SEARCH_LOCATIONS%"

rem set datalink log4j file location
set "DATALINK_LOG4J_OPTS=--logging.config=%BASE_DIR%/conf/datalink-logback.xml"


set COMMAND="%JAVA%" %DATALINK_JVM_OPTS% %DATALINK_OPTS% %DATALINK_CONFIG_OPTS% %DATALINK_LOG4J_OPTS% datalink.datalink %*

rem start datalink command
%COMMAND%
