package com.max.homon.kit.zk;

import com.max.homon.kit.zk.config.ZKConfig;
import com.max.homon.kit.zk.listener.ZKSuccessListener;
import com.max.homon.kit.zk.service.ZKClient;
import com.max.homon.kit.zk.service.ZKServiceRegistryAndDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Import({ZKConfig.class})
@Configuration
@ConditionalOnProperty(name = {"homon.zk.enable"}, matchIfMissing = false, havingValue = "true")
public class ZKAutoConfig {

    @Autowired
    private ZKConfig zkConfig;

    @Bean
    public ZKClient handler() {
        ZKClient client = new ZKClient(zkConfig);
        client.init();
        client.start(new ZKSuccessListener());
        return client;
    }


    @Bean
    public ZKServiceRegistryAndDiscovery zkServiceRegistryAndDiscovery(ZKClient zkClient){
        return new ZKServiceRegistryAndDiscovery(zkClient);
    }
}
