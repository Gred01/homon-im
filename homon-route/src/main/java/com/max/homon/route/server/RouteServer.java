package com.max.homon.route.server;

import com.max.homon.api.serivce.IListener;
import com.max.homon.kit.netty.api.connection.IConnectionManager;
import com.max.homon.kit.netty.base.AbstractNettyTcpServer;
import com.max.homon.route.Handler.ProxyHanlder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* 链接服务
*@Author Gred
*@Date 2020/3/16 22:55
*@version 1.0
**/
@Slf4j
@Component
public class RouteServer extends AbstractNettyTcpServer {

    @Autowired
    private IConnectionManager connectionManager;
    @Autowired
    private ProxyHanlder channelHandler;


    @Override
    public void init() {
        super.init();
        connectionManager.init();
    }

    /**
    * 初始化pipeline
    *@Author Gred
    *@Date 2020/3/16 22:55
    *@version 1.0
    **/
    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        super.initPipeline(pipeline);
    }

    /**
    * 初始化参数
    *@Author Gred
    *@Date 2020/3/16 22:55
    *@Param null
    **/
    @Override
    protected void initOptions(ServerBootstrap b) {
        super.initOptions(b);
        b.childOption(ChannelOption.SO_SNDBUF, 32*1024*1024);
        b.childOption(ChannelOption.SO_RCVBUF, 32*1024*1024);
        b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                32*1024*1024, 64*1024*1024
        ));
    }

    @Override
    public void start(IListener listener) {
        log.debug("[START][CONNECT-SERVER][ING]");
        super.start(listener);
        log.debug("[START][CONNECT-SERVER][SUCCESS]");
    }


    @Override
    public void stop(IListener listener) {
        super.stop(listener);

        connectionManager.destroy();
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

}
