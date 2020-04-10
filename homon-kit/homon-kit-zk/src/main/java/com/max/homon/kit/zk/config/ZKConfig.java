package com.max.homon.kit.zk.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "homon.zk")
public class ZKConfig {

    public static final int ZK_MAX_RETRY = 3;
    public static final int ZK_MIN_TIME = 5000;
    public static final int ZK_MAX_TIME = 5000;
    public static final int ZK_SESSION_TIMEOUT = 5000;
    public static final int ZK_CONNECTION_TIMEOUT = 5000;
    public static final String ZK_DEFAULT_CACHE_PATH = "/";

    private String hosts;

    private String digest;

    private String namespace;

    private int maxRetries = ZK_MAX_RETRY;

    private int baseSleepTimeMs = ZK_MIN_TIME;

    private int maxSleepMs = ZK_MAX_TIME;

    private int sessionTimeout = ZK_SESSION_TIMEOUT;

    private int connectionTimeout = ZK_CONNECTION_TIMEOUT;

    private String watchPath = ZK_DEFAULT_CACHE_PATH;


}
