package com.max.homon.biz.user.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo extends Model<UserInfo> {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String account;

    private String password;

    private String channel;

    private String userType;

    private String createTime;

    private String updateTime;
}
