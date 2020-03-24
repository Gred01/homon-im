package com.max.homon.core.exception;

import com.max.homon.api.exception.IException;
import lombok.Getter;
import lombok.Setter;


/**
* 业务异常
*@Author Gred
*@Date 2020/3/15 22:37
*@version 1.0
**/
@Setter
@Getter
public class BizException extends RuntimeException implements IException {


    public BizException(String message) {
        super(message);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

}
