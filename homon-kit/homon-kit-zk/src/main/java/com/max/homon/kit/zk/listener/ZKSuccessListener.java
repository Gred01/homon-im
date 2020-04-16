package com.max.homon.kit.zk.listener;


import com.max.homon.api.base.AbstractListener;
import com.max.homon.core.base.FutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ZKSuccessListener extends AbstractListener {


    @Override
    public void onSuccess(Object... args) {
        super.onSuccess(args);
        log.info("=========================================");
        log.info("ZK SUCCESS START ON PORT[{}]",args);
        log.info("=========================================");
    }

    @Override
    public void onFailure(Throwable cause) {
        super.onFailure(cause);
        log.info("=========================================");
        log.error("ZK SUCCESS FAILURE ON EXCEPTION[{}]",cause);
        log.info("=========================================");
    }
}
