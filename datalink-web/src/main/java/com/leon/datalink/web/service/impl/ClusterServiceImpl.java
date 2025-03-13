package com.leon.datalink.web.service.impl;

import com.leon.datalink.cluster.member.ClusterMember;
import com.leon.datalink.cluster.member.ClusterMemberManager;
import com.leon.datalink.web.service.ClusterService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Override
    public List<ClusterMember> list(ClusterMember clusterMember) {
        Stream<ClusterMember> stream = ClusterMemberManager.getList().stream();
        if (null != clusterMember) {
            if (null != clusterMember.getMemberName()) {
                stream = stream.filter(r -> r.getMemberName().contains(clusterMember.getMemberName()));
            }
            if (null != clusterMember.getMemberState()) {
                stream = stream.filter(r -> r.getMemberState().equals(clusterMember.getMemberState()));
            }
        }
        return stream.sorted(Comparator.comparing(ClusterMember::getUpdateTime)).collect(Collectors.toList());
    }

}
