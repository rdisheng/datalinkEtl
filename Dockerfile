# 基础镜像
FROM  openjdk:8-jre
# 作者
MAINTAINER leon9512
# 数据挂载点
VOLUME /datalink/data
# 备份挂载点
VOLUME /datalink/backup
# 插件挂载点
VOLUME /datalink/plugins
# 日志挂载点
VOLUME /datalink/logs
# 复制安装文件
COPY ./distribution/target/datalink-server-1.0.0.tar.gz /datalink-server.tar.gz
# 解压
RUN tar -zxvf /datalink-server.tar.gz -C /
# 启动
ENTRYPOINT java -jar '-Duser.timezone=GMT+08' '-Ddatalink.home=/datalink' /datalink/target/datalink-server.jar --spring.config.additional-location=/datalink/conf/ --logging.config=/datalink/conf/datalink-logback.xml