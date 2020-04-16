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

package com.max.homon.kit.zk.service;

import com.alibaba.fastjson.JSON;
import com.max.homon.api.serivce.IListener;
import com.max.homon.api.zk.IServiceDiscovery;
import com.max.homon.api.zk.IServiceListener;
import com.max.homon.api.zk.IServiceNode;
import com.max.homon.api.zk.IServiceRegistry;
import com.max.homon.core.base.AbstractService;
import com.max.homon.core.bean.zk.ServerNode;
import com.max.homon.kit.zk.listener.ZKCacheEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.curator.utils.ZKPaths.PATH_SEPARATOR;

/**
 * Created by ohun on 16/9/22.
 *
 * @author ohun@live.cn (夜色)
 */

public final class ZKServiceRegistryAndDiscovery extends
        AbstractService implements IServiceRegistry, IServiceDiscovery {

    private final ZKClient client;

    public ZKServiceRegistryAndDiscovery(ZKClient client){
        this.client  = client;
    }

    @Override
    public void start(IListener listener) {
        if (isRunning()) {
            listener.onSuccess();
        } else {
            super.start(listener);
        }
    }

    @Override
    public void stop(IListener listener) {
        if (isRunning()) {
            super.stop(listener);
        } else {
            listener.onSuccess();
        }
    }

    @Override
    protected void doStart(IListener listener) throws Throwable {
        client.start(listener);
    }

    @Override
    protected void doStop(IListener listener) throws Throwable {
        client.stop(listener);
    }

    @Override
    public void register(IServiceNode node) {
        if (node.isPersistent()) {
            client.registerPersist(node.nodePath(), JSON.toJSONString(node));
        } else {
            client.regEphemeralNodes(node.nodePath(), JSON.toJSONString(node));
        }
    }

    @Override
    public void deregister(IServiceNode node) {
        if (client.isRunning()) {
            client.remove(node.nodePath());
        }
    }

    @Override
    public List<IServiceNode> lookup(String serviceName) {
        List<String> childrenKeys = client.getChildrenKeys(serviceName);
        if (childrenKeys == null || childrenKeys.isEmpty()) {
            return Collections.emptyList();
        }

        return childrenKeys.stream()
                .map(key -> serviceName + PATH_SEPARATOR + key)
                .map(client::get)
                .filter(Objects::nonNull)
                .map(childData ->
                        JSON.parseObject(childData, ServerNode.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void subscribe(String path, IServiceListener listener) {
        client.registerListener(new ZKCacheEventListener(path,listener));
    }

    @Override
    public void unsubscribe(String path, IServiceListener listener) {

    }

}
