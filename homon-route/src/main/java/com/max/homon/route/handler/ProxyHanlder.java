package com.max.homon.route.handler;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.max.homon.core.bean.zk.CompareNode;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.connection.IConnectionManager;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.servie.connection.NettyConnection;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
@Component
@Scope("prototype")
public class ProxyHanlder extends ChannelInboundHandlerAdapter {

    @Autowired
    private IConnectionManager connectionManager;
    @Autowired
    private NettyConfig nettyConfig;
    @Autowired(required = false)
    private AllocHandler allocHandler;

    private Channel serverChannel;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("[OutChannelReceiveMsg][msg={}]", JSON.toJSONString(msg));
        IConnection connection = connectionManager.get(ctx.channel());
        connection.updateLastReadTime();

        if (serverChannel == null || !serverChannel.isActive()){
            initServerConnect(ctx);
        }

        serverChannel.writeAndFlush(msg)
                .addListener((ChannelFuture future)->{
            if (future.isSuccess()){
                // was able to flush out data, start to read the next chunk
                ctx.channel().read();
            }else{
                future.cause().printStackTrace();
                future.channel().close();
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[current class is][{}]",this);
        log.info("client connected conn={}", ctx.channel());

        //初始化server节点链接
        initServerConnect(ctx);

        IConnection connection = new NettyConnection();
        connection.init(ctx.channel(), false,nettyConfig);
        //connection.startHeartBeat();
        connectionManager.add(connection);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        IConnection connection = connectionManager.removeAndClose(ctx.channel());
        if (serverChannel != null && serverChannel.isActive()){
            serverChannel.close();
        }
        log.info("client disconnected conn={}", connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        IConnection connection = connectionManager.get(ctx.channel());
        cause.printStackTrace();
        log.error("client caught ex={}, conn={}",cause.getStackTrace(), connection);
        ctx.close();
    }

    public boolean initServerConnect(ChannelHandlerContext ctx){
        try {
            List<CompareNode> nodes = allocHandler.getServiceNodes();
            Assert.notEmpty(nodes);

            CompareNode node = nodes.get(0);
            // Start the connection attempt.
            Channel inboundChannel = ctx.channel();
            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop())
                    .channel(inboundChannel.getClass())
                    .handler(new ProxyClientHandler(inboundChannel))
                    .option(ChannelOption.AUTO_READ, false);
            ChannelFuture f = b.connect(node.getIp(), node.getPort());
            serverChannel = f.channel();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            });
        }catch (Exception e){
            log.error("[代理处理类][重连失败][error={}]",e);
        }
        return false;
    }

    private void closeOnFlush(Channel channel){
        if (channel.isActive()){
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
