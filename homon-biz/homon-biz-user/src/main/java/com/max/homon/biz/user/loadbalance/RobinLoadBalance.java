package com.max.homon.biz.user.loadbalance;

import cn.hutool.core.collection.CollectionUtil;
import com.max.homon.core.bean.vo.ChannelInfoVO;
import com.max.homon.kit.cache.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RobinLoadBalance extends AbstractLoadBalanceImpl{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public RedisAtomicInteger round;

    @PostConstruct
    private void init(){
        round = new RedisAtomicInteger("0",redisTemplate.getConnectionFactory());
    }

    @Override
    ChannelInfoVO loadBalance(List<ChannelInfoVO> channels) {
        int length = channels.size()-1;
        int target = length & round.getAndIncrement();
        return channels.get(target);
    }

    @Override
    public List<ChannelInfoVO> getAllServer() {
        return CollectionUtil.newArrayList(new ChannelInfoVO("127.0.0.1",6111));
    }
}
