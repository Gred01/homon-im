package com.max.homon.route.handler;

import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.connection.IConnectionManager;
import com.max.homon.kit.netty.api.message.IMessageDispatcher;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.servie.connection.NettyConnection;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public final class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {

    @Autowired
    private IConnectionManager connectionManager;
    @Autowired
    private IMessageDispatcher dispatcher;
    @Autowired
    private NettyConfig nettyConfig;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        IConnection connection = connectionManager.get(ctx.channel());
        connection.updateLastReadTime();
        dispatcher.onDispatcher(msg,connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        IConnection connection = connectionManager.get(ctx.channel());
        cause.printStackTrace();
        log.error("client caught ex={}, conn={}",cause, connection);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("client connected conn={}", ctx.channel());
        IConnection connection = new NettyConnection();
        connection.init(ctx.channel(), false,nettyConfig);
        //connection.startHeartBeat();
        connectionManager.add(connection);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        IConnection connection = connectionManager.removeAndClose(ctx.channel());
        log.info("client disconnected conn={}", connection);
    }

}
