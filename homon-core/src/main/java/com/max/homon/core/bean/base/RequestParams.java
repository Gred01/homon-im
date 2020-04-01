package com.max.homon.core.bean.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RequestParams<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求参数
     **/
    private T param;

    /**
     * 签名
     **/
    private String sign;

    /**
     * 时间戳
     **/
    private String timestamp;

    /**
     * 签名类型
     **/
    private String signType;

    /**
     * 加密类型
     **/
    private String encryptType;

    /**
     * 密文
     **/
    private String encryptData;

    /*** 客户端会话sessionId ***/
    private String sessionId;

    /*** 终端类型 ***/
    private String termType;

    /*** 请求流水号 ***/
    private String reqNo;

    /**
     * 接口版本号
     **/
    private String version;
}
