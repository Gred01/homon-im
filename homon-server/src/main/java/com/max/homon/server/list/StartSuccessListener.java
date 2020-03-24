package com.max.homon.server.list;

import com.max.homon.api.serivce.IListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartSuccessListener implements IListener {

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
