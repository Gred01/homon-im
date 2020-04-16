package com.max.homon.route.handler;

import com.alibaba.fastjson.JSON;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.servie.connection.NettyConnection;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ProxyClientHandler extends ChannelInboundHandlerAdapter {

    private IConnection connection;

    private Channel routeChannel;

    public ProxyClientHandler(Channel routeChannel){
        this.routeChannel = routeChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("[InChannelReceiveMsg][msg={}]", JSON.toJSONString(msg));
        routeChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.channel().read();
            } else {
                future.channel().close();
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
        log.debug("[client][成功建立连接]");
        connection = new NettyConnection();
        connection.init(ctx.channel(),false,new NettyConfig());
        connection.updateLastWriteTime();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("[client][链接异常，关闭链接][错误信息为：{}]",cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        log.debug("[client][链接被关闭][重连]");
    }
}
