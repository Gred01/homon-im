package com.max.homon.core.enums;


/**
* Connection状态
*@Author Gred
*@Date 2020/3/16 22:53
*@version 1.0
**/
public enum ConnectionStatus {

    STATUS_NEW(1),
    STATUS_CONNECTED(2),
    STATUS_DISCONNECTED(3);

    public final int type;

    ConnectionStatus(int i) {
        this.type = i;
    }

}
