package com.max.homon.core.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginVO<T> {

    private String sessionId;
    private T user;
    private ChannelInfoVO channel;
}
