package com.max.homon.route.handler;

import com.max.homon.api.zk.IServiceNode;
import com.max.homon.core.bean.zk.CompareNode;

import java.util.List;

public interface ISortHandler {

    List<CompareNode> sorted(List<IServiceNode> nodes);
}
