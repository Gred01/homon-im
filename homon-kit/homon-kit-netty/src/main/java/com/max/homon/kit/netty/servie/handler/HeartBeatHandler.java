package com.max.homon.kit.netty.servie.handler;

import com.max.homon.kit.netty.annotation.CmdHandler;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.message.IMessageHandler;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.protocol.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* 心跳beat
*@Author Gred
*@Date 2020/3/17 15:48
*@version 1.0
**/
@Slf4j
@Component
@CmdHandler(cmd = Command.HEARTBEAT)
public class HeartBeatHandler implements IMessageHandler {

    @Override
    public void handle(Packet packet, IConnection connection) {
        //ping -> pong
        connection.send(packet);
        log.info("ping -> pong, {}", connection);
    }
}
