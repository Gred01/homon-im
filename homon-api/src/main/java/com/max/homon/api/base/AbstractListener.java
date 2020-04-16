package com.max.homon.api.base;

import com.max.homon.api.serivce.IListener;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public abstract class AbstractListener implements IListener {

    protected AbstractListener next;

    @Override
    public void onSuccess(Object... args) {
        if (next != null) {
            log.info("start AbstractListener [{}]", getNextName());
            next.onSuccess(args);
        }
    }

    @Override
    public void onFailure(Throwable cause) {
        if (next != null) {
            next.onFailure(cause);
            log.info("stopped AbstractListener [{}]", getNextName());
        }
    }

    @Override
    public void onClosed(Object... args) {
        if (next != null) {
            next.onClosed(args);
            log.info("stopped AbstractListener [{}]", getNextName());
        }
    }

    public AbstractListener setNext(AbstractListener next) {
        this.next = next;
        return next;
    }

    public AbstractListener setNext(Supplier<AbstractListener> next, boolean enabled) {
        if (enabled) {
            return setNext(next.get());
        }
        return this;
    }

    protected String getNextName() {
        return next.getName();
    }

    protected String getName() {
        return this.getClass().getSimpleName();
    }
}
