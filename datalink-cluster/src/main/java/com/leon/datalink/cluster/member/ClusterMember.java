package com.leon.datalink.cluster.member;

import com.leon.datalink.cluster.constants.ClusterMemberStateEnum;

import java.io.Serializable;

public class ClusterMember implements Serializable {

    private String memberName;

    private ClusterMemberStateEnum memberState;

    private String updateTime;

    private boolean isLocal;

    public String getMemberName() {
        return memberName;
    }

    public ClusterMember setMemberName(String memberName) {
        this.memberName = memberName;
        return this;
    }

    public ClusterMemberStateEnum getMemberState() {
        return memberState;
    }

    public ClusterMember setMemberState(ClusterMemberStateEnum memberState) {
        this.memberState = memberState;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public ClusterMember setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public ClusterMember setLocal(boolean local) {
        isLocal = local;
        return this;
    }
}
