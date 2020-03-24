package com.max.homon.core.bean;

import lombok.Data;

/**
* 客户端配置
*@Author Gred
*@Date 2020/3/23 14:20
*@version 1.0
**/
@Data
public class ClientConfig {

    private byte[] clientKey;
    private byte[] iv;
    private String clientVersion;
    private String deviceId;
    private String osName;
    private String osVersion;
    private String userId;
    private String cipher;

}
