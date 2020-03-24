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

package com.max.homon.kit.netty.servie.connection;

import com.max.homon.core.enums.ConnectionStatus;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.base.SessionContext;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.servie.task.HeartBeatTask;
import com.max.homon.kit.netty.servie.task.WheelTimer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by ohun on 2015/12/22.
 *
 * @author ohun@live.cn
 */
@Slf4j
public final class NettyConnection implements IConnection, ChannelFutureListener {

    private SessionContext context;
    private Channel channel;
    private volatile byte status = (byte) ConnectionStatus.STATUS_NEW.type;
    private long lastReadTime;
    private long lastWriteTime;
    private NettyConfig config;

    @Override
    public void init(Channel channel, boolean security) {
        this.init(channel,security,null);
    }

    @Override
    public void init(Channel channel, boolean security, NettyConfig config) {
        this.channel = channel;
        this.config = config;
        this.context = new SessionContext();
        context.setHeartbeat(config.getConnect().getCheckSeconds());

        this.lastReadTime = System.currentTimeMillis();
        this.status = (byte) ConnectionStatus.STATUS_CONNECTED.type;
    }

    public void init(Channel channel, NettyConfig config) {
        this.channel = channel;
        this.context = new SessionContext();
        context.setHeartbeat(config.getConnect().getCheckSeconds());

        this.lastReadTime = System.currentTimeMillis();
        this.status = (byte) ConnectionStatus.STATUS_CONNECTED.type;
    }

    @Override
    public void startHeartBeat(){
        if (config.getConnect().isEnabledHeartBeat()){
            WheelTimer.I.get().newTimeout(new HeartBeatTask(this)
                    ,this.context.heartbeat, TimeUnit.SECONDS);
        }else{
            log.debug("[current not enabled heartbeat]");
        }
    }

    @Override
    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    @Override
    public SessionContext getSessionContext() {
        return context;
    }

    @Override
    public String getId() {
        return channel.id().asShortText();
    }

    @Override
    public ChannelFuture send(Packet packet) {
        return send(packet, null);
    }

    @Override
    public ChannelFuture send(Packet packet, final ChannelFutureListener listener) {
        if (channel.isActive()) {

            ChannelFuture future = channel.writeAndFlush(packet.toFrame(channel)).addListener(this);

            if (listener != null) {
                future.addListener(listener);
            }

            if (channel.isWritable()) {
                return future;
            }

            //阻塞调用线程还是抛异常？
            //return channel.newPromise().setFailure(new RuntimeException("send data too busy"));
            if (!future.channel().eventLoop().inEventLoop()) {
                future.awaitUninterruptibly(100);
            }
            return future;
        } else {
            /*if (listener != null) {
                channel.newPromise()
                        .addListener(listener)
                        .setFailure(new RuntimeException("connection is disconnected"));
            }*/
            return this.close();
        }
    }

    @Override
    public ChannelFuture close() {
        if (status == ConnectionStatus.STATUS_DISCONNECTED.type) {
            return null;
        }
        this.status = (byte) ConnectionStatus.STATUS_DISCONNECTED.type;
        return this.channel.close();
    }

    @Override
    public boolean isConnected() {
        return status == ConnectionStatus.STATUS_CONNECTED.type;
    }

    @Override
    public boolean isReadTimeout() {
        return System.currentTimeMillis() - lastReadTime > config.getConnect().getReadTimeouts()*1000;
    }

    @Override
    public boolean isWriteTimeout() {
        return System.currentTimeMillis() - lastWriteTime > config.getConnect().getWriteTimeouts()*1000;
    }

    @Override
    public void updateLastReadTime() {
        lastReadTime = System.currentTimeMillis();
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            lastWriteTime = System.currentTimeMillis();
        } else {
            log.error("connection send msg error={}, conn={}", future.cause().getMessage(), this);
        }
    }

    @Override
    public void updateLastWriteTime() {
        lastWriteTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[channel=" + channel
                + ", context=" + context
                + ", status=" + status
                + ", lastReadTime=" + lastReadTime
                + ", lastWriteTime=" + lastWriteTime
                + "]";
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NettyConnection that = (NettyConnection) o;

        return channel.id().equals(that.channel.id());
    }

    @Override
    public int hashCode() {
        return channel.id().hashCode();
    }
}
