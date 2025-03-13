package com.leon.datalink.cluster.member;

import akka.cluster.Member;
import cn.hutool.core.date.DateTime;
import com.leon.datalink.cluster.constants.ClusterMemberStateEnum;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;

import java.util.*;

/**
 * 集群成员
 */
public class ClusterMemberManager {

    private static final Map<String, ClusterMember> members = new HashMap<>();

    private static String localMemberName;

    static {
        // 单机模式下创建本地节点
        if (!EnvUtil.isCluster()) {
            setLocalMemberName("datalink@127.0.0.1");
            ClusterMember clusterMember = new ClusterMember();
            clusterMember.setMemberName(localMemberName);
            clusterMember.setMemberState(ClusterMemberStateEnum.UP);
            clusterMember.setLocal(true);
            clusterMember.setUpdateTime(DateTime.now().toString());
            members.put(localMemberName, clusterMember);
        }
    }

    public static void up(Member member, boolean local) {
        String name = getMemberName(member);
        ClusterMember clusterMember = new ClusterMember();
        clusterMember.setMemberName(name);
        clusterMember.setMemberState(ClusterMemberStateEnum.UP);
        clusterMember.setUpdateTime(DateTime.now().toString());
        clusterMember.setLocal(local);
        members.put(name, clusterMember);
        if(local){
           setLocalMemberName(getMemberName(member));
        }
    }

    public static void down(Member member) {
        String name = getMemberName(member);
        ClusterMember clusterMember = members.get(name);
        if (null == clusterMember) return;
        clusterMember.setMemberState(ClusterMemberStateEnum.DOWN);
        clusterMember.setUpdateTime(DateTime.now().toString());
    }

    public static List<ClusterMember> getList() {
        Collection<ClusterMember> values = members.values();
        return new ArrayList<>(values);
    }

    public static String getMemberName(Member member) {
        return member.address().toString().replace("akka://", "");
    }

    public static String getLocalMemberName() {
        return localMemberName;
    }

    public static void setLocalMemberName(String localMemberName) {
        ClusterMemberManager.localMemberName = localMemberName;
        // 加入环境变量
        GlobalVariableContent.setValue("localMemberName", localMemberName);
    }
}
