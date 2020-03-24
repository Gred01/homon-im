package com.max.homon.kit.netty.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ToString
@Component
@ConfigurationProperties(prefix = "homon.netty")
public class NettyConfig {

    private ThreadConfig thread = new ThreadConfig();
    private Connect connect = new Connect();
    private Client client = new Client();
    private Integer ioRate = 70;
    private Integer port = 6111;
    private String host = "127.0.0.1";

    @Setter
    @Getter
    @ToString
    public class ThreadConfig{
        private Integer bosscnt = 1;
        private Integer workcnt = Runtime.getRuntime().availableProcessors();
    }

    @Setter
    @Getter
    @ToString
    public class Connect{
        private boolean enabledHeartBeat = true;
        private int maxTimeOuts = 3;
        /*** 超时检测时间 ***/
        private int checkSeconds = 5;
        /*** 读取超时时间，定时检测链接的活性,如果长时间没活动，就超时关闭 ***/
        private int readTimeouts = checkSeconds*2;
        /*** 写超时时间，避免对方误测，所以也需要定时推送心跳 ***/
        private int writeTimeouts = 4;
    }

    @Setter
    @Getter
    @ToString
    public class Client{
        /*** 客户端端口 ***/
        private Integer port = 9100;
        /*** 客户端主机 ***/
        private String host = "127.0.0.1";
    }
}

