package com.max.homon.server.listener;

import com.max.homon.api.base.AbstractListener;
import com.max.homon.core.bean.zk.ServerNode;
import com.max.homon.core.enums.ZKPaths;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.utils.SpringContextHolder;
import com.max.homon.kit.zk.service.ZKClient;
import com.max.homon.kit.zk.service.ZKServiceRegistryAndDiscovery;
import org.springframework.context.annotation.DependsOn;

import javax.swing.*;

@DependsOn("springContextHolder")
public class Reg2ZKListener extends AbstractListener {

    private ZKServiceRegistryAndDiscovery register = SpringContextHolder.getBean(ZKServiceRegistryAndDiscovery.class);

    private NettyConfig config = SpringContextHolder.getBean(NettyConfig.class);

    private ServerNode serverNode;

    @Override
    public void onSuccess(Object... args) {
        register.register(getServerNode());
        super.onSuccess(args);
    }

    @Override
    public void onFailure(Throwable cause) {
        super.onFailure(cause);
        register.deregister(getServerNode());
    }

    @Override
    public void onClosed(Object... args) {
        super.onClosed(args);
        register.deregister(getServerNode());
    }


    public ServerNode getServerNode(){
        serverNode = new ServerNode();
        serverNode.setName(ZKPaths.CONN_SERVER.getPath())
                .setPort(config.getPort())
                .setHost(config.getHost())
                .setPersistent(false);
        return serverNode;
    }

}
