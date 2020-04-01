package com.max.homon.biz.user;

import com.max.homon.kit.cache.CacheApplication;
import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {CacheApplication.class, UserApplication.class})
public class UserApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(UserApplication.class,args);
    }
}
