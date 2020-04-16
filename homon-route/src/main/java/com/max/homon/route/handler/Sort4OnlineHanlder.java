package com.max.homon.route.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.max.homon.api.zk.IServiceNode;
import com.max.homon.core.bean.zk.CompareNode;
import com.max.homon.core.constant.ZKConstant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("sort4OnlineHanlder")
public class Sort4OnlineHanlder implements ISortHandler {

    private CompareNode convert(IServiceNode node) {
        String public_ip = node.getAttr(ZKConstant.ZK_ATTR_PUBLIC_IP);
        if (public_ip == null) {
            public_ip = node.getHost();
        }
        long onlineUserNum = getOnlineUserNum(public_ip);
        return new CompareNode(public_ip, node.getPort(), onlineUserNum);
    }

    @Override
    public List<CompareNode> sorted(List<IServiceNode> nodes) {
        if (CollectionUtil.isEmpty(nodes)){
            return Lists.newArrayList();
        }
        return nodes.stream()
                .map(this::convert)
                .sorted(CompareNode::compareTo)
                .collect(Collectors.toList());
    }

    private int getOnlineUserNum(String ip){
        //todo 这边后续要统计每个server节点在线用户数
        return 1;
    }
}
