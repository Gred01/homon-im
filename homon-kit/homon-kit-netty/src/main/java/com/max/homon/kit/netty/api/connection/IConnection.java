package com.max.homon.kit.netty.api.connection;

import com.max.homon.kit.netty.base.SessionContext;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.protocol.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
* 自定义Connection对象，维护每个Channel信息以及业务处理
*@Author Gred
*@Date 2020/3/16 22:34
*@version 1.0
**/
public interface IConnection<T extends NettyConfig> {

    void init(Channel channel, boolean security);

    void init(Channel channel, boolean security , T config);

    /*** 获取当前session上下文 ***/
    SessionContext getSessionContext();

    /*** 设置当前sessionId上下文 ***/
    void setSessionContext(SessionContext context);

    ChannelFuture send(Packet packet);

    ChannelFuture send(Packet packet, ChannelFutureListener listener);

    String getId();

    /*** 关闭 ***/
    ChannelFuture close();

    boolean isConnected();

    boolean isReadTimeout();

    boolean isWriteTimeout();

    void updateLastReadTime();

    void updateLastWriteTime();

    Channel getChannel();

    /*** 开始心跳检测 ***/
    void startHeartBeat();
}
