package com.max.homon.route.listenr;

import com.max.homon.api.serivce.IListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartSuccessListener implements IListener {

    @Override
    public void onSuccess(Object... args) {
        log.info("=========================================");
        log.info("Start RouteServer Successs on Port [{}]",args);
        log.info("=========================================");
    }

    @Override
    public void onFailure(Throwable cause) {
        log.info("=========================================");
        log.info("Start RouteServer Failure");
        log.info("=========================================");
    }
}
