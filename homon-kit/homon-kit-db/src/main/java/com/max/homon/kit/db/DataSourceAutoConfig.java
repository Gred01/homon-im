package com.max.homon.kit.db;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.max.homon.kit.db.aop.DataSourceAOP;
import com.max.homon.kit.db.constant.DataSourceKey;
import com.max.homon.kit.db.util.DynamicDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * @author 作者 owen 
 * @version 创建时间：2017年04月23日 下午20:01:06 类说明
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 * 在设置了spring.datasource.enable.dynamic 等于true是开启多数据源，配合日志
 */
@MapperScan("com.max.homon.*.mapper*")
@PropertySource("mybatisplus.properties")
@Configuration
@Import(DataSourceAOP.class)
@AutoConfigureBefore(value={DruidDataSourceAutoConfigure.class,MybatisPlusAutoConfiguration.class})
@ConditionalOnProperty(name = {"spring.datasource.dynamic.enable"}, matchIfMissing = false, havingValue = "true")
public class DataSourceAutoConfig {
 
    /*** 核心数据库 ***/
	@Bean
	@ConfigurationProperties("spring.datasource.druid.core")
	public DataSource dataSourceCore(){
	    return DruidDataSourceBuilder.create().build();
	}

	/*** 所有的核心库共享一个日志中心模块，改模块不采用mysql中的innodb引擎，采用归档引擎 ***/
	@Bean
	@ConfigurationProperties("spring.datasource.druid.log")
	public DataSource dataSourceLog(){
	    return DruidDataSourceBuilder.create().build();
	}
	

	/*** 只需要纳入动态数据源到spring容器 ***/
	@Primary
    @Bean
    public DataSource dataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        DataSource coreDataSource =  dataSourceCore() ;
        DataSource logDataSource =  dataSourceLog();
        dataSource.addDataSource(DataSourceKey.core, coreDataSource);
        dataSource.addDataSource(DataSourceKey.log, logDataSource);
        dataSource.setDefaultTargetDataSource(coreDataSource);
        return dataSource;
    }


    /*** 将数据源纳入spring事物管理 ***/
    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource")  DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
   
}
