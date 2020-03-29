package com.max.homon.kit.cache.util;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
* redis分布式锁
*@Author Gred
*@Date 2020/1/21 17:57
*@version 1.0
**/
@Component
public final class RedisLockUtil {


    @Autowired
    private RedisUtil redisUtil;

    //锁键
    private String lock_value = "lock_value";
    //锁过期时间
    protected long internalLockLeaseTime = 10L;
    //监听锁过期时间
    protected long lockListenTime = internalLockLeaseTime+1L;

    //获取锁的超时时间
    private long timeout = 5L;

    private ConcurrentHashMap<String,ScheduledFuture> futureMap = new ConcurrentHashMap<>();


    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(4);

    /**
     * 开启定时刷新
     */
    protected void scheduleExpirationRenewal(String key){
        ScheduledFuture future =
                executorService.schedule(new ExpirationRenewal(key),lockListenTime, TimeUnit.SECONDS);
        futureMap.put(key,future);
    }

    /**
     * 刷新key的过期时间
     */
    private class ExpirationRenewal implements Runnable{

        private String key;

        public ExpirationRenewal(String key){
            this.key = key;
        }

        @SneakyThrows
        @Override
        public void run() {
            if (redisUtil.hasKey(key)){
                System.out.println("执行延迟失效时间中...");
                redisUtil.expire(key,internalLockLeaseTime);
            }
        }
    }

    /**
     * 加锁
     * @param id
     * @return
     */
    public boolean lock(String id){

        Long start = System.currentTimeMillis();
        for(;;){
            //SET命令返回OK ，则证明获取锁成功
            boolean lock = redisUtil.set(id,lock_value, internalLockLeaseTime);
            if(lock){
                scheduleExpirationRenewal(id);
                //开启延迟线程，判断10s后是否释放锁，没释放则继续持有锁
                return lock;
            }

            //否则循环等待，在timeout时间内仍未获取到锁，则获取失败
            long l = System.currentTimeMillis() - start;
            if (l>=timeout) {
                return false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 解锁
     * @param id
     * @return
     */
    public void unlock(String id){
        //如果提前释放锁，就删除定时任务
        redisUtil.del(id);
        ScheduledFuture future = futureMap.get(id);
        if (future != null){
            future.cancel(false);
        }
    }
}
