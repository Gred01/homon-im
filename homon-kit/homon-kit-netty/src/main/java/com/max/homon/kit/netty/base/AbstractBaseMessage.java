package com.max.homon.kit.netty.base;

import com.alibaba.fastjson.JSON;
import com.max.homon.core.utils.IOUtils;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.message.IMessage;
import com.max.homon.kit.netty.protocol.Packet;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
* 基础消息体
*@Author Gred
*@Date 2020/3/16 23:46
*@version 1.0
**/
@Setter
@Getter
public abstract class AbstractBaseMessage implements IMessage {

    private static final byte STATUS_DECODED = 1;
    private static final byte STATUS_ENCODED = 2;
    private static final AtomicInteger ID_SEQ = new AtomicInteger();
    transient public Packet packet;
    transient public IConnection connection;
    transient private byte status = 0;

    public AbstractBaseMessage(Packet packet, IConnection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    @Override
    public void decodeBody() {
        if ((status & STATUS_DECODED) == 0) {
            status |= STATUS_DECODED;

            if (packet.getBodyLength() > 0) {
                if (packet.hasFlag(Packet.FLAG_JSON_BODY)) {
                    decodeJsonBody0();
                } else {
                    decodeBinaryBody0();
                }
            }

        }
    }

    @Override
    public void encodeBody() {
        if ((status & STATUS_ENCODED) == 0) {
            status |= STATUS_ENCODED;

            if (packet.hasFlag(Packet.FLAG_JSON_BODY)) {
                encodeJsonBody0();
            } else {
                encodeBinaryBody0();
            }
        }

    }

    private void decodeBinaryBody0() {
        //1.解密
        byte[] tmp = packet.body;
       /* if (packet.hasFlag(Packet.FLAG_CRYPTO)) {
            if (getCipher() != null) {
                tmp = getCipher().decrypt(tmp);
            }
        }*/
        //2.解压
        if (packet.hasFlag(Packet.FLAG_COMPRESS)) {
            tmp = IOUtils.decompress(tmp);
        }

        if (tmp.length == 0) {
            throw new RuntimeException("message decode ex");
        }

        packet.body = tmp;
        decode(packet.body);
        packet.body = null;// 释放内存
    }

    private void encodeBinaryBody0() {
        byte[] tmp = encode();
        if (tmp != null && tmp.length > 0) {
            //1.压缩
            if (tmp.length > 1024*1024) {
                byte[] result = IOUtils.compress(tmp);
                if (result.length > 0) {
                    tmp = result;
                    packet.addFlag(Packet.FLAG_COMPRESS);
                }
            }

            //todo 加密
            packet.body = tmp;
        }
    }

    private void decodeJsonBody0() {
        Map<String, Object> body = packet.getBody();
        decodeJsonBody(body);
    }

    private void encodeJsonBody0() {
        packet.setBody(encodeJsonBody());
    }

    private void encodeJsonStringBody0() {
        packet.setBody(encodeJsonStringBody());
    }

    protected String encodeJsonStringBody() {
        return JSON.toJSONString(this);
    }

    private void encodeBodyRaw() {
        if ((status & STATUS_ENCODED) == 0) {
            status |= STATUS_ENCODED;

            if (packet.hasFlag(Packet.FLAG_JSON_BODY)) {
                encodeJsonBody0();
            } else {
                packet.body = encode();
            }
        }
    }

    public abstract void decode(byte[] body);

    public abstract byte[] encode();

    protected void decodeJsonBody(Map<String, Object> body) {

    }

    protected Map<String, Object> encodeJsonBody() {
        return null;
    }

    @Override
    public Packet getPacket() {
        return packet;
    }

    @Override
    public IConnection getConnection() {
        return connection;
    }

    @Override
    public void send(ChannelFutureListener listener) {
        encodeBody();
        connection.send(packet, listener);
    }

    @Override
    public void sendRaw(ChannelFutureListener listener) {
        encodeBodyRaw();
        connection.send(packet, listener);
    }

    public void send() {
        send(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendRaw() {
        sendRaw(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void close() {
        send(ChannelFutureListener.CLOSE);
    }

    protected static int genSessionId() {
        return ID_SEQ.incrementAndGet();
    }

    public int getSessionId() {
        return packet.sessionId;
    }

    public AbstractBaseMessage setRecipient(InetSocketAddress recipient) {
        packet.setRecipient(recipient);
        return this;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public void setConnection(IConnection connection) {
        this.connection = connection;
    }

    public ScheduledExecutorService getExecutor() {
        return connection.getChannel().eventLoop();
    }

    public void runInRequestThread(Runnable runnable) {
        connection.getChannel().eventLoop().execute(runnable);
    }

    @Override
    public abstract String toString();
}
