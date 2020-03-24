package com.max.homon.kit.netty.servie.handler;

import com.max.homon.kit.netty.annotation.CmdHandler;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.base.AbstractBaseHandler;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.servie.message.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
* 
*@Author Gred
*@Date 2020/3/17 15:55
*@version 1.0
**/
@Slf4j
@Component
@CmdHandler(cmd = Command.CHAT)
public class ChatHandler extends AbstractBaseHandler<ChatMessage> {

    @Override
    public ChatMessage decode(Packet packet, IConnection connection) {
        return new ChatMessage(Command.CHAT,packet,connection);
    }

    @Override
    public void handle(ChatMessage message) {
        //查询当前绑定链接和对象
        log.debug("[Chat={}]收到消息,内容=[{}]",message.connection.getChannel().id(),message.toString());
    }
}
