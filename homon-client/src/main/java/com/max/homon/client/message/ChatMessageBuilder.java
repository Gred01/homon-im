package com.max.homon.client.message;

import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.servie.message.ChatMessage;

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
