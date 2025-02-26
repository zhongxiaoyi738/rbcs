package com.hsbc.common.cache;

import com.alicp.jetcache.anno.SerialPolicy;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.hsbc.common.base.RbcsException;
import com.hsbc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Function;

@Slf4j
public class JacksonPolicy implements SerialPolicy {

    public static final String NAME = "jacksonPolicy";

    private static final ObjectMapper objectMapper = JsonUtils.getObjectMapper().copy();

    static {
        // 配置ObjectMapper以包含类信息序列化
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return value -> {
            if (value == null) {
                return null;
            }
            byte[] result;
            try {
                result = objectMapper.writeValueAsBytes(value);
            } catch (Exception e) {
                log.error("JetCache JacksonPolicy encoder 转换异常，value: {}", JsonUtils.toJson(value), e);
                throw RbcsException.of("转换异常");
            }
            log.debug("JetCache 发送缓存更新 value: {}", JsonUtils.toJson(value));
            return result;
        };
    }

    @Override
    public Function<byte[], Object> decoder() {
        return value -> {
            if (value == null) {
                return null;
            }
            Object result;
            try {
                result = objectMapper.readValue(value, Object.class);
            } catch (Exception e) {
                throw RbcsException.of("JetCache JacksonPolicy decoder 转换异常");
            }
            log.debug("JetCache 收到缓存更新 result: {}", JsonUtils.toJson(result));
            return result;
        };
    }

}
