package com.max.homon.kit.netty.servie.task;

import com.max.homon.core.constant.ThreadNames;
import com.max.homon.kit.netty.thread.NamedPoolThreadFactory;
import io.netty.util.HashedWheelTimer;

/**
* 定时工具类
*@Author Gred
*@Date 2020/3/20 17:14
*@version 1.0
**/
public enum WheelTimer {

    I;

    public HashedWheelTimer get(){
        return new HashedWheelTimer(new NamedPoolThreadFactory(ThreadNames.T_CONN_TIMER));
    }
}
