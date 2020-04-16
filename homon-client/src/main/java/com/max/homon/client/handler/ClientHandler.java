package com.max.homon.client.handler;

import com.max.homon.client.service.ConnClientBoot;
import com.max.homon.core.bean.ClientConfig;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.servie.connection.NettyConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private IConnection connection;
    private ConnClientBoot boot;

    public ClientHandler(ConnClientBoot boot){
        this.boot = boot;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        connection.updateLastReadTime();
        if (msg instanceof Packet){
            Packet packet = (Packet) msg;
            Command command = Command.toCMD(packet.cmd);
            switch (command){
                case HEARTBEAT:
                    log.debug("[client收到心跳消息][消息={}]",packet.toString());
                    break;
                case CHAT:
                    log.debug("[client收到聊天消息][消息={}]",packet.toString());
                    break;
                default:
                    log.debug("[client收到消息][消息={}]",packet.toString());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("[client][成功建立连接]");
        connection = new NettyConnection();
        connection.init(ctx.channel(),false,new NettyConfig());
        //connection.startHeartBeat();
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
        boot.connect(new ClientConfig(),true);
    }
}
