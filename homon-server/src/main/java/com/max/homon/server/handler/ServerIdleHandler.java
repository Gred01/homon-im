package com.max.homon.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Deprecated
public class ServerIdleHandler extends IdleStateHandler {

    public ServerIdleHandler(){
        super(10,0,0,TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.info("[读取超时][关闭当前连接]");
            ctx.close();
            return;
        }

        super.channelIdle(ctx,evt);
    }
}
