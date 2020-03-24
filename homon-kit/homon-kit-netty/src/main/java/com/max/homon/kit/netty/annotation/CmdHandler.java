package com.max.homon.kit.netty.annotation;


import com.max.homon.kit.netty.protocol.Command;

import java.lang.annotation.*;

/**
 * @author Boom
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdHandler {

    Command cmd();
}
