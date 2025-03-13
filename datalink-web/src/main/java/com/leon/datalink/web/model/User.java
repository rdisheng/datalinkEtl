

package com.leon.datalink.web.model;

import java.io.Serializable;
import java.util.List;

/**
 * User.
 *
 * @author Leon
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 3371769277802700069L;
    
    private String username;
    
    private String password;

    private String description;

    private List<String> permissions;

    private String createTime;

    private boolean system; // 系统用户 （全部权限、不可删除）

    public User() {
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public User setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public User setPermissions(List<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public User setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public boolean isSystem() {
        return system;
    }

    public User setSystem(boolean system) {
        this.system = system;
        return this;
    }
}
