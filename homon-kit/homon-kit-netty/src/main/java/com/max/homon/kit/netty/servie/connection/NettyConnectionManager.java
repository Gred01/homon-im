/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */

package com.max.homon.kit.netty.servie.connection;

import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.connection.IConnectionManager;
import com.max.homon.kit.netty.servie.task.WheelTimer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ohun on 2016/12/27.
 *
 * @author ohun@live.cn (夜色)
 */
@Slf4j
@Component
public final class NettyConnectionManager implements IConnectionManager {

    private final ConcurrentMap<ChannelId, IConnection> connections = new ConcurrentHashMap<>();

    @Override
    public IConnection get(Channel channel) {
        return connections.get(channel.id());
    }

    @Override
    public IConnection removeAndClose(Channel channel) {
        IConnection connection = connections.remove(channel.id());
        if (connection != null){
            connection.close();
        }

        //add default
        connection = new NettyConnection();
        connection.init(channel, false);
        connection.close();
        return connection;
    }

    @Override
    public void add(IConnection connection) {
        connections.putIfAbsent(connection.getChannel().id(), connection);
    }

    @Override
    public int getConnNum() {
        return connections.size();
    }

    @Override
    public void init() {

        log.debug("init netty Connection Manager ");
    }

    @Override
    public void destroy() {
        if (WheelTimer.I.get() != null) {
            WheelTimer.I.get().stop();
        }
        connections.values().forEach(IConnection::close);
        connections.clear();
    }
}
