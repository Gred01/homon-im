package com.max.homon.core.enums;

import lombok.Getter;

/**
* 消息分发策略
*@Author Gred
*@Date 2020/3/16 23:06
*@version 1.0
**/
@Getter
public enum DispatchPolicy {
    REJECT(2),LOG(1),IGNORE(0);
    private int level;


    DispatchPolicy(int i) {
        this.level = i;
    }

    public static DispatchPolicy find(int level) {
        for (DispatchPolicy type : values()) {
            if (type.level == level) {
                return type;
            }
        }
        return IGNORE;
    }

}
