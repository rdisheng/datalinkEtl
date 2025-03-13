package com.leon.datalink.web.model;

/**
 * @ClassName SystemInfo
 * @Description
 * @Author Leon
 * @Date2022/4/11 10:35
 * @Version V1.0
 **/
public class SystemInfo {

    private String version;

    private String localMemberName;

    private String time;

    private String username;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocalMemberName() {
        return localMemberName;
    }

    public void setLocalMemberName(String localMemberName) {
        this.localMemberName = localMemberName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
