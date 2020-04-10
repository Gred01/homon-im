package com.max.homon.route;

import com.max.homon.core.base.FutureListener;
import com.max.homon.kit.netty.NettyApplicaion;
import com.max.homon.route.listenr.StartSuccessListener;
import com.max.homon.route.server.RouteServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackageClasses = {
        RouteApplication.class,
        NettyApplicaion.class,
})
public class RouteApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RouteApplication.class,args);

        RouteServer connectServer = context.getBean(RouteServer.class);
        connectServer.init();
        connectServer.start(new FutureListener(new StartSuccessListener(),new AtomicBoolean(false)));
    }
}
