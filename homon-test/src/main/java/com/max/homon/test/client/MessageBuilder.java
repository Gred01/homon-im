package com.max.homon.test.client;

import com.max.homon.kit.netty.base.AbstractBaseMessage;

public abstract class MessageBuilder<T extends AbstractBaseMessage> {

    protected abstract T toMessage(String object);
}
