package com.max.homon.server;

import com.max.homon.kit.netty.NettyApplicaion;
import com.max.homon.server.listener.Reg2ZKListener;
import com.max.homon.server.listener.StartSuccessListener;
import com.max.homon.server.service.ConnectServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@Slf4j
@SpringBootApplication
@ComponentScan(basePackageClasses = {
        ServerApplicaion.class,
        NettyApplicaion.class,
})
public class ServerApplicaion {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(ServerApplicaion.class,args);

        ConnectServer connectServer = context.getBean(ConnectServer.class);
        connectServer.init();
        connectServer.start(new StartSuccessListener()
                .setNext(new Reg2ZKListener()));
    }
}
