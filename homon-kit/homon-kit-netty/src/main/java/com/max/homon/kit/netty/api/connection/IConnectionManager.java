package com.max.homon.kit.netty.api.connection;

import io.netty.channel.Channel;

/**
* Connection管理类
*@Author Gred
*@Date 2020/3/16 22:35
*@version 1.0
**/
public interface IConnectionManager {

    IConnection get(Channel channel);

    IConnection removeAndClose(Channel channel);

    void add(IConnection connection);

    int getConnNum();

    void init();

    void destroy();
}
