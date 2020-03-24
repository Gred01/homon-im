/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */

package com.max.homon.test.client;

import com.max.homon.api.serivce.IListener;
import com.max.homon.core.bean.ClientConfig;
import com.max.homon.kit.netty.base.AbstractService;
import com.max.homon.kit.netty.codec.PacketDecoder;
import com.max.homon.kit.netty.codec.PacketEncoder;
import com.max.homon.kit.netty.config.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class ConnClientBoot extends AbstractService {

    public static final AttributeKey<ClientConfig> CONFIG_KEY = AttributeKey.newInstance("clientConfig");
    private Bootstrap bootstrap;
    private NioEventLoopGroup workerGroup;
    private NettyConfig nettyConfig;
    private IListener listener;

    public ConnClientBoot(NettyConfig config){
        this.nettyConfig = config;
    }

    @Override
    protected void doStart(IListener listener) throws Throwable {

        this.workerGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60 * 1000)
                .option(ChannelOption.SO_RCVBUF, 5 * 1024 * 1024)
                .channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new PacketDecoder());
                ch.pipeline().addLast("encoder", PacketEncoder.INSTANCE);
                ch.pipeline().addLast("handler", new ClientHandler(ConnClientBoot.this));
            }
        });

        this.listener = listener;
    }

    @Override
    protected void doStop(IListener listener) throws Throwable {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        listener.onFailure(null);
    }

    public ChannelFuture connect(InetSocketAddress remote, InetSocketAddress local,
                                 ClientConfig clientConfig,boolean needReConnect) {
        ChannelFuture future = local != null ?
                bootstrap.connect(remote, local) : bootstrap.connect(remote);
        if (future.channel() != null) {
            future.channel().attr(CONFIG_KEY).set(clientConfig);
        }
        future.addListener(f -> {
            if (f.isSuccess()) {
                future.channel().attr(CONFIG_KEY).set(clientConfig);
                listener.onSuccess();
            } else {
                listener.onFailure(f.cause());
                log.error("start netty client failure, remote={}, local={}", remote, local, f.cause());
                if (needReConnect){
                    future.channel().eventLoop().schedule(() ->
                                    connect(remote,local,clientConfig,needReConnect),
                            10, TimeUnit.SECONDS);
                    log.error("Failed to connect to server, try connect after 10s, remote={}, local={}", remote, local);
                }
            }
        });
        return future;
    }

    public ChannelFuture connect(String host, int port, ClientConfig clientConfig,boolean needReConnect) {
        return connect(new InetSocketAddress(host, port), null, clientConfig,needReConnect);
    }

    public ChannelFuture connect(ClientConfig clientConfig,boolean needReConnect) {

        InetSocketAddress remoteAddress = new
                InetSocketAddress(nettyConfig.getHost(), nettyConfig.getPort());
        InetSocketAddress localAddress = new
                InetSocketAddress(nettyConfig.getClient().getHost(), nettyConfig.getClient().getPort());

        return connect(remoteAddress, localAddress, clientConfig,needReConnect);
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public NioEventLoopGroup getWorkerGroup() {
        return workerGroup;
    }
}