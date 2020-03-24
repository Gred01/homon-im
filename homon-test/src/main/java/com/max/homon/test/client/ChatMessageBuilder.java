package com.max.homon.test.client;

import com.max.homon.core.utils.IOUtils;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.servie.message.ChatMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ChatMessageBuilder extends MessageBuilder<ChatMessage> {

    public static final ChatMessageBuilder I = new ChatMessageBuilder();
    @Override
    public ChatMessage toMessage(String object) {
        ChatMessage chatMessage = new ChatMessage(Command.CHAT, (byte) 0,object);
       /* ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        try {
            chatMessage.encode(byteBuf);
        }finally {
            byteBuf.release();
        }*/
        return chatMessage;
    }
}
