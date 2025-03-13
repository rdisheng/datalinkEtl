package com.leon.datalink.web.service;


import com.leon.datalink.cluster.member.ClusterMember;

import java.util.List;


public interface ClusterService {

    List<ClusterMember> list(ClusterMember clusterMember);

}
