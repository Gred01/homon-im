package com.max.homon.kit.netty.base;

import com.max.homon.api.serivce.IListener;
import com.max.homon.api.serivce.IServer;
import com.max.homon.core.base.AbstractService;
import com.max.homon.core.constant.ThreadNames;
import com.max.homon.core.enums.ServiceStatus;
import com.max.homon.core.exception.BizException;
import com.max.homon.kit.netty.codec.PacketDecoder;
import com.max.homon.kit.netty.codec.PacketEncoder;
import com.max.homon.kit.netty.config.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
* 
*@Author Gred
*@Date 2020/3/15 22:54
*@version 1.0
**/
@Slf4j
@Component
public abstract class AbstractNettyTcpServer extends AbstractService implements IServer {

    @Autowired
    private NettyConfig nettyConfig;

    /*** 当前服务状态 ***/
    private AtomicReference<ServiceStatus> status = new AtomicReference<>(ServiceStatus.Created);

    private int port;
    private String host;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private IListener listener;


    @Override
    public void init() {
        if (!status.compareAndSet(ServiceStatus.Created,ServiceStatus.Initialized)){
            throw new BizException("Server already init");
        }

        port = nettyConfig.getPort();
        host = nettyConfig.getHost();
    }

    @Override
    public boolean isRunning() {
        return status.equals(ServiceStatus.Started);
    }

    @Override
    public void start(IListener listener) {
        if (!status.compareAndSet(ServiceStatus.Initialized,ServiceStatus.Starting)){
            throw new BizException("Server already started or have not init");
        }
        if (Epoll.isAvailable()) {
            createEpollServer(listener);
        } else {
            createNioServer(listener);
        }
    }

    @Override
    public void stop(IListener listener) {
        super.stop(listener);
        if (!status.compareAndSet(ServiceStatus.Started,ServiceStatus.Shutdown)){
            if (listener != null) {
                listener.onFailure(new BizException("server was already shutdown."));
            }
            log.error("{} was already shutdown.", this.getClass().getSimpleName());
            return;
        }

        //关闭服务
        if (bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null){
            workGroup.shutdownGracefully();
        }
        if (listener !=null){
            listener.onSuccess(port);
        }
    }

    /**
     * 创建nio的服务
     *@Author Gred
     *@Date 2020/3/15 23:29
     *@version 1.0
     **/
    private void createNioServer(IListener listener){

        EventLoopGroup bossGroup = getBossGroup();
        EventLoopGroup workerGroup = getWorkGroup();

        if (bossGroup == null) {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(nettyConfig.getThread().getBosscnt(), getBossThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(100);
            bossGroup = nioEventLoopGroup;
        }

        if (workerGroup == null) {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(nettyConfig.getThread().getBosscnt(), getWorkThreadFactory(), getSelectorProvider());
            nioEventLoopGroup.setIoRatio(nettyConfig.getIoRate());
            workerGroup = nioEventLoopGroup;
        }

        createServer(listener, bossGroup, workerGroup, getChannelFactory());
    };

    /**
    * 创建epoll的服务
    *@Author Gred
    *@Date 2020/3/15 23:29
    *@version 1.0
    **/
    private void createEpollServer(IListener listener) {
        EventLoopGroup bossGroup = getBossGroup();
        EventLoopGroup workerGroup = getWorkGroup();

        if (bossGroup == null) {
            EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(nettyConfig.getThread().getBosscnt(), getBossThreadFactory());
            epollEventLoopGroup.setIoRatio(100);
            bossGroup = epollEventLoopGroup;
        }

        if (workerGroup == null) {
            EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(nettyConfig.getThread().getWorkcnt(), getWorkThreadFactory());
            epollEventLoopGroup.setIoRatio(nettyConfig.getIoRate());
            workerGroup = epollEventLoopGroup;
        }

        createServer(listener, bossGroup, workerGroup, EpollServerSocketChannel::new);
    }


    private void createServer(IListener listener, EventLoopGroup boss, EventLoopGroup work, ChannelFactory<? extends ServerChannel> channelFactory) {
        /***
         * NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器，
         * Netty提供了许多不同的EventLoopGroup的实现用来处理不同传输协议。
         * 在一个服务端的应用会有2个NioEventLoopGroup会被使用。
         * 第一个经常被叫做‘boss’，用来接收进来的连接。
         * 第二个经常被叫做‘worker’，用来处理已经被接收的连接，
         * 一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
         * 如何知道多少个线程已经被使用，如何映射到已经创建的Channels上都需要依赖于EventLoopGroup的实现，
         * 并且可以通过构造函数来配置他们的关系。
         */
        this.bossGroup = boss;
        this.workGroup = work;

        try {
            /**
             * ServerBootstrap 是一个启动NIO服务的辅助启动类
             * 你可以在这个服务中直接使用Channel
             */
            ServerBootstrap b = new ServerBootstrap();

            /**
             * 这一步是必须的，如果没有设置group将会报java.lang.IllegalStateException: group not set异常
             */
            b.group(bossGroup, workGroup);

            /***
             * ServerSocketChannel以NIO的selector为基础进行实现的，用来接收新的连接
             * 这里告诉Channel如何获取新的连接.
             */
            b.channelFactory(channelFactory);


            /***
             * 这里的事件处理类经常会被用来处理一个最近的已经接收的Channel。
             * ChannelInitializer是一个特殊的处理类，
             * 他的目的是帮助使用者配置一个新的Channel。
             * 也许你想通过增加一些处理类比如NettyServerHandler来配置一个新的Channel
             * 或者其对应的ChannelPipeline来实现你的网络程序。
             * 当你的程序变的复杂时，可能你会增加更多的处理类到pipeline上，
             * 然后提取这些匿名类到最顶层的类上。
             */
            b.childHandler(new ChannelInitializer<Channel>() { // (4)
                @Override
                public void initChannel(Channel ch) throws Exception {
                    initPipeline(ch.pipeline());
                }
            });

            initOptions(b);

            /***
             * 绑定端口并启动去接收进来的连接
             */
            InetSocketAddress address = StringUtils.isBlank(host)
                    ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
            b.bind(address).addListener(future -> {
                if (future.isSuccess()) {
                    status.set(ServiceStatus.Started);
                    if (listener != null) {
                        listener.onSuccess(port);
                    }
                } else {
                    log.error("server start failure on:{}", port, future.cause());
                    if (listener != null) {
                        listener.onFailure(future.cause());
                    }
                }
            });
        } catch (Exception e) {
            log.error("server start exception", e);
            if (listener != null) {
                listener.onFailure(e);
            }
            throw new BizException("server start exception, port=" + port, e);
        }
    }

    /**
     * 每连上一个链接调用一次
     *
     * @param pipeline
     */
    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("protobufDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("decoder", getDecoder());
        pipeline.addLast("protobufEecoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("encoder", getEncoder());
        pipeline.addLast("handler", getChannelHandler());
    }


    /***
     * option()是提供给NioServerSocketChannel用来接收进来的连接。
     * childOption()是提供给由父管道ServerChannel接收到的连接，
     * 在这个例子中也是NioServerSocketChannel。
     */
    protected void initOptions(ServerBootstrap b) {
        //b.childOption(ChannelOption.SO_KEEPALIVE, false);// 使用应用层心跳

        /**
         * 在Netty 4中实现了一个新的ByteBuf内存池，它是一个纯Java版本的 jemalloc （Facebook也在用）。
         * 现在，Netty不会再因为用零填充缓冲区而浪费内存带宽了。不过，由于它不依赖于GC，开发人员需要小心内存泄漏。
         * 如果忘记在处理程序中释放缓冲区，那么内存使用率会无限地增长。
         * Netty默认不使用内存池，需要在创建客户端或者服务端的时候进行指定
         */
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }


    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkGroup() {
        return workGroup;
    }

    public ChannelFactory<? extends ServerChannel> getChannelFactory() {
        return NioServerSocketChannel::new;
    }

    public SelectorProvider getSelectorProvider() {
        return SelectorProvider.provider();
    }

    /**
     * netty 默认的Executor为ThreadPerTaskExecutor
     * 线程池的使用在SingleThreadEventExecutor#doStartThread
     * <p>
     * eventLoop.execute(runnable);
     * 是比较重要的一个方法。在没有启动真正线程时，
     * 它会启动线程并将待执行任务放入执行队列里面。
     * 启动真正线程(startThread())会判断是否该线程已经启动，
     * 如果已经启动则会直接跳过，达到线程复用的目的
     *
     * @return
     */
    protected ThreadFactory getBossThreadFactory() {
        return new DefaultThreadFactory(getBossThreadName());
    }

    protected ThreadFactory getWorkThreadFactory() {
        return new DefaultThreadFactory(getWorkThreadName());
    }


    protected String getBossThreadName() {
        return ThreadNames.T_BOSS;
    }

    protected String getWorkThreadName() {
        return ThreadNames.T_WORKER;
    }


    protected ChannelHandler getDecoder() {
        return new PacketDecoder();
    }

    //每连上一个链接调用一次, 所有用单例
    protected ChannelHandler getEncoder() {
        return PacketEncoder.INSTANCE;
    }

    public abstract ChannelHandler getChannelHandler();
}
