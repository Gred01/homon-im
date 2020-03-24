package com.max.homon.kit.netty.servie.task;

import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.config.NettyConfig;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.utils.SpringContextHolder;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;

import java.util.concurrent.TimeUnit;

@Slf4j
@DependsOn("springContextHolder")
public class HeartBeatTask implements TimerTask {

    private IConnection connection;

    private NettyConfig config = SpringContextHolder.getBean(NettyConfig.class);

    /*** 超时次数 ***/
    private int timeOuts = 0;

    public HeartBeatTask(IConnection connection){
        this.connection = connection;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (isValidConnection() && heartCheck()){
            timeout.timer().newTimeout(this,config.getConnect().getCheckSeconds(), TimeUnit.SECONDS);
        }
    }

    private boolean heartCheck() {

        //如果读超时，累计超时数，超限关闭链接
        if (connection.isReadTimeout()){
            timeOuts++;
            log.warn("[heartbeat timeout][times={}][client={}]", timeOuts, connection);
        }else{
            timeOuts = 0;
        }

        if (timeOuts >= config.getConnect().getMaxTimeOuts()){
            log.warn("[heartbeat timeout][times={} over limit={}][client={}]", timeOuts,config.getConnect().getMaxTimeOuts(), connection);
            timeOuts = 0;
            connection.close();
            return false;
        }

        //如果写超时，发送心跳、
        if (connection.isWriteTimeout()){
            log.info("send heartbeat ping...");
            connection.send(Packet.HB_PACKET);
        }

        return true;
    }

    /*** 是否是合法连接 ***/
    public boolean isValidConnection(){
        return connection != null && connection.isConnected();
    }
}
