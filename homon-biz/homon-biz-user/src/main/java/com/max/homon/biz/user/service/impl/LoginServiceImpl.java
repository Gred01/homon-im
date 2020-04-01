package com.max.homon.biz.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.max.homon.biz.user.entity.UserInfo;
import com.max.homon.biz.user.loadbalance.RobinLoadBalance;
import com.max.homon.biz.user.mapper.IUserInfoMapper;
import com.max.homon.biz.user.service.ILoginService;
import com.max.homon.core.bean.bo.LoginBO;
import com.max.homon.core.bean.vo.LoginVO;
import com.max.homon.core.bean.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginServiceImpl  extends ServiceImpl<IUserInfoMapper, UserInfo> implements ILoginService {

    @Autowired
    private RobinLoadBalance robinLoadBalance;

    @Override
    public LoginVO login(LoginBO req) {
        Assert.notBlank(req.getAccount());
        Assert.notBlank(req.getPassword());

        QueryWrapper<UserInfo> queryWrapper =  new QueryWrapper<>();
        queryWrapper.lambda().eq(UserInfo::getAccount,req.getAccount());
        UserInfo userInfo = this.getOne(queryWrapper);
        Assert.notNull(userInfo,"用户信息不对");

        //对比密码
        String password = SecureUtil.md5(req.getPassword());
        Assert.state(userInfo.getPassword().equals(password),"密码不对");

        LoginVO<UserInfoVO> loginVO = new LoginVO<>();
        loginVO.setUser(BeanUtil.copyProperties(userInfo,UserInfoVO.class));
        loginVO.setSessionId(IdUtil.simpleUUID());
        loginVO.setChannel(robinLoadBalance.getBestOne());
        return loginVO;
    }

}
