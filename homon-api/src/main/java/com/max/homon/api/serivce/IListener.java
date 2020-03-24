package com.max.homon.api.serivce;

/**
* 服务监听
*@Author Gred
*@Date 2020/3/15 22:29
*@version 1.0
**/
public interface IListener {

    void onSuccess(Object... args);

    void onFailure(Throwable cause);
}