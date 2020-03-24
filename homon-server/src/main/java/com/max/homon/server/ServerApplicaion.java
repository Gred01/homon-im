package com.max.homon.server;

import com.max.homon.kit.netty.NettyApplicaion;
import com.max.homon.kit.netty.servie.FutureListener;
import com.max.homon.server.list.StartSuccessListener;
import com.max.homon.server.service.ConnectServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.atomic.AtomicBoolean;


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
        connectServer.start(new FutureListener(new StartSuccessListener(),new AtomicBoolean(false)));
    }
}
