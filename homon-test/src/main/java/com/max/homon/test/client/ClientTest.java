package com.max.homon.test.client;

import com.max.homon.api.serivce.IListener;
import com.max.homon.core.bean.ClientConfig;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.codec.PacketDecoder;
import com.max.homon.kit.netty.codec.PacketEncoder;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.servie.connection.NettyConnection;
import com.max.homon.kit.netty.servie.message.ChatMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Slf4j
public class ClientTest {

    public static void main(String[] args) throws Throwable {

        NettyConfig config = new NettyConfig();
        config.setPort(8011);
        ConnClientBoot boot = new ConnClientBoot(config);
        ClientConfig clientConfig = new ClientConfig();
        boot.doStart(new IListener() {
            @Override
            public void onSuccess(Object... args) {
                log.debug("[client][启动成功！！！]");
            }

            @Override
            public void onFailure(Throwable cause) {
                log.debug("[client][启动失败！！！]");
            }

            @Override
            public void onClosed(Object... args) {

            }
        });
        ChannelFuture future = boot.connect(clientConfig,false).sync();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        IConnection<NettyConfig> connection = new NettyConnection();
        connection.init(future.channel(),false,new NettyConfig());
        while(true){
            String content = in.readLine();
            Packet packet = new Packet(Command.CHAT,1);
            ChatMessage chatMessage = new ChatMessage(Command.CHAT, packet,connection);
            chatMessage.setData(content);
            if (!chatMessage.getConnection().getChannel().isActive()){
                connection = new NettyConnection();
                connection.init(boot.getChannel(),false,new NettyConfig());
                chatMessage.setConnection(connection);
            }
            chatMessage.sendRaw();
        }

    }

}
