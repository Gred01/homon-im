package com.max.homon.kit.cache;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"redis.properties"})
public class CacheApplication {
}
