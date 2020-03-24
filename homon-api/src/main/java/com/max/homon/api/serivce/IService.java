package com.max.homon.api.serivce;

import java.util.concurrent.CompletableFuture;

/**
* 基础netty服务
*@Author Gred
*@Date 2020/3/15 22:29
*@version 1.0
**/
public interface IService {

    void start(IListener listener);

    void stop(IListener listener);

    CompletableFuture<Boolean> start();

    CompletableFuture<Boolean> stop();

    boolean syncStart();

    boolean syncStop();

    void init();

    boolean isRunning();

}
