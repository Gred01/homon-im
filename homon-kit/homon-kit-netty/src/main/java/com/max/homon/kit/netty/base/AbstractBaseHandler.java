package com.max.homon.kit.netty.base;

import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.message.IMessageHandler;
import com.max.homon.kit.netty.protocol.Packet;

/**
* 抽象基础处理类
*@Author Gred
*@Date 2020/3/17 15:56
*@version 1.0
**/
public abstract class AbstractBaseHandler<T extends AbstractBaseMessage> implements IMessageHandler {

    /*** 解码 ***/
    public abstract T decode(Packet packet, IConnection connection);

    public abstract void handle(T message);

    @Override
    public void handle(Packet packet, IConnection connection) {
        T t = decode(packet,connection);
        if (t != null) {
            t.decodeBody();
            handle(t);
        }
    }
}
