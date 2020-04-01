package com.max.homon.core.bean.bo;

import com.max.homon.core.bean.base.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginBO extends BaseBO {

    private String account;
    private String password;
}
