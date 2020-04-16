package com.max.homon.core.enums;

import lombok.Getter;
import lombok.Setter;

/**
* ZK路径枚举
*@Author Gred
*@Date 2020/4/12 22:46
*@version 1.0
**/
@Getter
public enum ZKPaths {

    CONN_SERVER("/cluster/cs"),
    ROUTE_SERVER("/cluster/rs");

    private String path;

    ZKPaths(String path) {
        this.path = path;
    }
}
