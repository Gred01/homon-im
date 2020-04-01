package com.max.homon.kit.db.annotation;

import java.lang.annotation.*;


/**
 * 数据源选择
 * @author owen
 * @create 2017年7月2日
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
	//数据库名称
    String name();
}