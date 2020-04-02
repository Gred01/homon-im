package com.max.homon.biz.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.max.homon.biz.user.entity.UserInfo;
import com.max.homon.core.bean.bo.LoginBO;
import com.max.homon.core.bean.vo.LoginVO;

public interface ILoginService extends IService<UserInfo> {

    LoginVO login(LoginBO req);

    LoginVO getUserBySessionId(String sessionId);
}
