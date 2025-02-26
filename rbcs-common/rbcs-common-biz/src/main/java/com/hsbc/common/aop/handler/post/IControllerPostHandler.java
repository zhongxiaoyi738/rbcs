package com.hsbc.common.aop.handler.post;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;

public interface IControllerPostHandler extends Ordered {

    Object postHandle(ProceedingJoinPoint joinPoint, Object result);

}
