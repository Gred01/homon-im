package com.max.homon.kit.netty.servie.message;

import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.base.AbstractByteBufMessage;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.protocol.Packet;
import io.netty.buffer.ByteBuf;
import lombok.Setter;

@Setter
public final class ChatMessage extends AbstractByteBufMessage {

    public Command cmd;
    public byte code;
    public String data;

    public ChatMessage(Command cmd,Packet message, IConnection connection) {
        super(message, connection);
        this.cmd = cmd;
    }

    public ChatMessage(Command cmd,byte code,String data) {
        super(null, null);
        this.cmd = cmd;
        this.code = code;
        this.data = data;
    }

    @Override
    public void decode(ByteBuf body) {
        cmd = Command.toCMD(decodeByte(body));
        code = decodeByte(body);
        data = decodeString(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeByte(body, cmd.cmd);
        encodeByte(body, code);
        encodeString(body, data);
    }



    @Override
    public String toString() {
        return "ChatMessage{" +
                "cmd=" + cmd +
                ", code=" + code +
                ", data='" + data + '\'' +
                '}';
    }
}
