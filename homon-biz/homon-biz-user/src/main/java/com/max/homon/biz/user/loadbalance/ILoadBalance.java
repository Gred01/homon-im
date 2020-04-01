package com.max.homon.biz.user.loadbalance;

import com.max.homon.core.bean.vo.ChannelInfoVO;

import java.util.List;

/**
* 负载均衡
*@Author Gred
*@Date 2020/4/1 22:56
*@version 1.0
**/
public interface ILoadBalance {

    List<ChannelInfoVO> getAllServer();

    ChannelInfoVO getBestOne();
}
