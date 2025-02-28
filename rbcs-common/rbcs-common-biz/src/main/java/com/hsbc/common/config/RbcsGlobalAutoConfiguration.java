package com.hsbc.common.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.hsbc.common.aop.handler.ControllerHandlerExecutor;
import com.hsbc.common.aop.handler.post.IControllerPostHandler;
import com.hsbc.common.aop.handler.pre.IControllerPreHandler;
import com.hsbc.log.idworker.IdWorker;
import com.hsbc.log.idworker.IdWorkerUtils;
import com.hsbc.common.utils.sm4.SmUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Configuration
@ComponentScan("com.hsbc.common")
@RequiredArgsConstructor
//@EnableFeignClients(basePackages = {"com.hsbc.*.feign", "com.hsbc.*.api"})
@EnableMethodCache(basePackages = {"com.hsbc"})
public class RbcsGlobalAutoConfiguration {
    @Value("${jasypt.encryptor.password}")
    private String jasyptEncryptorPassword;

    @Resource
    private RedissonClient redissonClient;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    private final List<IControllerPreHandler> controllerPreHandlers;

    private final List<IControllerPostHandler> controllerPostHandlers;

    @Bean
//    @ConditionalOnBean(RedissonClient.class)
    public IdWorker idWorker() {
        RAtomicLong atomicLong = redissonClient.getAtomicLong("rbcs:workId");
        return new IdWorker(atomicLong.getAndIncrement());
    }

    @Bean
    @ConditionalOnBean(IdWorker.class)
    public IdWorkerUtils idWorkerUtils(IdWorker idWorker) {
        return new IdWorkerUtils(idWorker);
    }

    @Bean
    public SmUtils smUtils() {
        return new SmUtils(jasyptEncryptorPassword);
    }

    @Bean
    public ControllerHandlerExecutor controllerHandlerExecutor() {
        // 初始化ControllerHandlerExecutor，配置预处理和后处理处理器
        return new ControllerHandlerExecutor(controllerPreHandlers, controllerPostHandlers);
    }

    @PostConstruct
    public void init() {
        if (buildProperties == null || !log.isDebugEnabled()) {
            return;
        }
        LocalDateTime buildTime = LocalDateTime.ofInstant(buildProperties.getTime(), ZoneId.systemDefault());
        log.debug("rbcs 构建信息\nname: {}\nversion: {}\nbuild-time: {}",
                buildProperties.getName(), buildProperties.getVersion(), buildTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    }
}
