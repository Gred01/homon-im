package com.max.homon.server.service;

import com.max.homon.api.serivce.IListener;
import com.max.homon.core.bean.zk.ServerNode;
import com.max.homon.core.enums.ZKPaths;
import com.max.homon.kit.netty.api.connection.IConnectionManager;
import com.max.homon.kit.netty.base.AbstractNettyTcpServer;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.server.handler.ServerChannelHandler;
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
public class ConnectServer extends AbstractNettyTcpServer {

    @Autowired
    private IConnectionManager connectionManager;
    @Autowired
    private ServerChannelHandler channelHandler;
    @Autowired
    private NettyConfig nettyConfig;

    private ServerNode serverNode;

    @Override
    public void init() {
        super.init();
        connectionManager.init();

        serverNode = new ServerNode();
        //初始化节点
        serverNode.setHost(nettyConfig.getHost())
                .setPort(nettyConfig.getPort())
                .setPersistent(false)
                .setName(ZKPaths.CONN_SERVER.getPath());
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

        /**
         * TCP层面的接收和发送缓冲区大小设置，
         * 在Netty中分别对应ChannelOption的SO_SNDBUF和SO_RCVBUF，
         * 需要根据推送消息的大小，合理设置，对于海量长连接，通常32K是个不错的选择。
         */
        b.childOption(ChannelOption.SO_SNDBUF, 32*1024*1024);
        b.childOption(ChannelOption.SO_RCVBUF, 32*1024*1024);

        /**
         * 这个坑其实也不算坑，只是因为懒，该做的事情没做。一般来讲我们的业务如果比较小的时候我们用同步处理，等业务到一定规模的时候，一个优化手段就是异步化。
         * 异步化是提高吞吐量的一个很好的手段。但是，与异步相比，同步有天然的负反馈机制，也就是如果后端慢了，前面也会跟着慢起来，可以自动的调节。
         * 但是异步就不同了，异步就像决堤的大坝一样，洪水是畅通无阻。如果这个时候没有进行有效的限流措施就很容易把后端冲垮。
         * 如果一下子把后端冲垮倒也不是最坏的情况，就怕把后端冲的要死不活。
         * 这个时候，后端就会变得特别缓慢，如果这个时候前面的应用使用了一些无界的资源等，就有可能把自己弄死。
         * 那么现在要介绍的这个坑就是关于Netty里的ChannelOutboundBuffer这个东西的。
         * 这个buffer是用在netty向channel write数据的时候，有个buffer缓冲，这样可以提高网络的吞吐量(每个channel有一个这样的buffer)。
         * 初始大小是32(32个元素，不是指字节)，但是如果超过32就会翻倍，一直增长。
         * 大部分时候是没有什么问题的，但是在碰到对端非常慢(对端慢指的是对端处理TCP包的速度变慢，比如对端负载特别高的时候就有可能是这个情况)的时候就有问题了，
         * 这个时候如果还是不断地写数据，这个buffer就会不断地增长，最后就有可能出问题了(我们的情况是开始吃swap，最后进程被linux killer干掉了)。
         * 为什么说这个地方是坑呢，因为大部分时候我们往一个channel写数据会判断channel是否active，但是往往忽略了这种慢的情况。
         *
         * 那这个问题怎么解决呢？其实ChannelOutboundBuffer虽然无界，但是可以给它配置一个高水位线和低水位线，
         * 当buffer的大小超过高水位线的时候对应channel的isWritable就会变成false，
         * 当buffer的大小低于低水位线的时候，isWritable就会变成true。所以应用应该判断isWritable，如果是false就不要再写数据了。
         * 高水位线和低水位线是字节数，默认高水位是64K，低水位是32K，我们可以根据我们的应用需要支持多少连接数和系统资源进行合理规划。
         */
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

    public ServerNode getServerNode() {
        return serverNode;
    }
}
