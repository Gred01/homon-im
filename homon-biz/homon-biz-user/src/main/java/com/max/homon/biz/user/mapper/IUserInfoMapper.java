package com.max.homon.biz.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.max.homon.biz.user.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
* 用户信息表 Mapper 接口
*@Author Gred
*@Date 2020/4/1 16:06
*@version 1.0
**/
@Mapper
public interface IUserInfoMapper extends BaseMapper<UserInfo> {

}
