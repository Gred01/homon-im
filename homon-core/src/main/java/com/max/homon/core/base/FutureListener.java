package com.max.homon.core.base;

import com.max.homon.api.serivce.IListener;
import com.max.homon.core.exception.BizException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FutureListener extends CompletableFuture<Boolean> implements IListener {


    private final IListener listener;
    private final AtomicBoolean started;

    public FutureListener(AtomicBoolean started) {
        this.started = started;
        this.listener = null;
    }

    public FutureListener(IListener listener, AtomicBoolean started) {
        this.listener = listener;
        this.started = started;
    }

    @Override
    public void onSuccess(Object... args) {
        if (isDone()) {
            return;// 防止Listener被重复执行
        }
        complete(started.get());
        if (listener != null) {
            listener.onSuccess(args);
        }
    }

    @Override
    public void onFailure(Throwable cause) {
        if (isDone()) {
            return;// 防止Listener被重复执行
        }
        completeExceptionally(cause);
        if (listener != null) {
            listener.onFailure(cause);
        }
        throw cause instanceof BizException
                ? (BizException) cause
                : new BizException(cause);
    }

    /**
     * 防止服务长时间卡在某个地方，增加超时监控
     *
     * @param service 服务
     */
    public void monitor(AbstractService service) {
        if (isDone()) {
            return;// 防止Listener被重复执行
        }
        runAsync(() -> {
            try {
                this.get(service.timeoutMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                this.onFailure(new BizException(String.format("service %s monitor timeout", service.getClass().getSimpleName())));
            }
        });
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

}
