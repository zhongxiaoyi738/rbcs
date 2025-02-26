package com.hsbc.common.aop.handler.pre;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;

public interface IControllerPreHandler extends Ordered {

    void preHandle(ProceedingJoinPoint joinPoint);

}
