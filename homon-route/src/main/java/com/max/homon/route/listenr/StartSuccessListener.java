package com.max.homon.route.listenr;

import com.max.homon.api.base.AbstractListener;
import com.max.homon.core.base.FutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class StartSuccessListener extends AbstractListener {

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
