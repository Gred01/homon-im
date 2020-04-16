package com.max.homon.server.listener;

import com.max.homon.api.base.AbstractListener;
import com.max.homon.core.base.FutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class StartSuccessListener extends AbstractListener {


    @Override
    public void onSuccess(Object... args) {
        log.info("######################################3");
        log.info("Start NettyServer Successs");
        log.info("######################################3");
    }

    @Override
    public void onFailure(Throwable cause) {
        log.info("######################################3");
        log.info("Start NettyServer Failure");
        log.info("######################################3");
    }
}
