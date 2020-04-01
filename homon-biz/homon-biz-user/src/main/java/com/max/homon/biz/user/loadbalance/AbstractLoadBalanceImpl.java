package com.max.homon.biz.user.loadbalance;


import cn.hutool.core.collection.CollectionUtil;
import com.max.homon.core.bean.vo.ChannelInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public abstract class AbstractLoadBalanceImpl implements ILoadBalance {

    @Override
    public List<ChannelInfoVO> getAllServer() {
        return null;
    }


    @Override
    public ChannelInfoVO getBestOne() {
        List<ChannelInfoVO> channels = this.getAllServer();
        if (CollectionUtil.isEmpty(channels)){
            return null;
        }

        return loadBalance(channels);
    }

    abstract ChannelInfoVO loadBalance(List<ChannelInfoVO> channels);
}
