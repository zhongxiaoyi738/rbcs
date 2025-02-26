package com.hsbc.trade;

import com.hsbc.common.config.RbcsGlobalAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration(RbcsGlobalAutoConfiguration.class)
@MapperScan(basePackages = "com.hsbc.trade.mapper")
public class TradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }

}

