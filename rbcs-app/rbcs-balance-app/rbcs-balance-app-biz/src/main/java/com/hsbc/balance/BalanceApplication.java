package com.hsbc.balance;

import com.hsbc.common.config.RbcsGlobalAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration(RbcsGlobalAutoConfiguration.class)
@MapperScan(basePackages = {"com.hsbc.balance.mapper", "com.hsbc.common.base"})
public class BalanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BalanceApplication.class, args);
    }

}

