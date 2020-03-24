package com.max.homon.core.enums;

import lombok.Getter;

@Getter
public enum RespType {

    ERROR(9999,"123"),
    SUCCESS(0000,"123");

    public short code;
    public String msg;

    RespType(int i, String s) {
        return ;
    }
}
