package com.max.homon.server.config;

import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.servie.FutureListener;
import com.max.homon.server.list.StartSuccessListener;
import com.max.homon.server.service.ConnectServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.atomic.AtomicBoolean;

/*
@Primary
@Configuration
public class InitConfig {

    @Autowired
    private NettyConfig nettyConfig;

    @Bean
    public ConnectServer connectServer(){
        ConnectServer connectServer = new ConnectServer(nettyConfig);
        connectServer.init();
        connectServer.start(new FutureListener(new StartSuccessListener(),new AtomicBoolean(false)));
        return connectServer;
    }
}
*/
