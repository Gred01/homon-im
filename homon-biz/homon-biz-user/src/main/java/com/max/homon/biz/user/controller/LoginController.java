package com.max.homon.biz.user.controller;

import com.max.homon.biz.user.service.ILoginService;
import com.max.homon.core.bean.base.RequestParams;
import com.max.homon.core.bean.base.ResponseParams;
import com.max.homon.core.bean.bo.LoginBO;
import com.max.homon.core.enums.RespType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/account",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LoginController {


    @Autowired
    private ILoginService loginService;

    @PostMapping("/login")
    public ResponseParams login(@RequestBody RequestParams<LoginBO> requestParams) {

        ResponseParams responseParams = ResponseParams.buildResponseParams(requestParams, RespType.SUCCESS);
        responseParams.setParam(loginService.login(requestParams.getParam()));
        return responseParams;
    }

}
