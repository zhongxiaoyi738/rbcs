package com.hsbc.common.aop;

import com.hsbc.common.aop.handler.ControllerHandlerExecutor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class RbcsControllerAop {

    @Pointcut("execution(public * com.hsbc..*.controller..*.* (..)))")
    public void controllerPointcut() {
    }

    /**
     * Controller环绕通知
     *      1、（@todo）记录日志
     *      2、（@todo）前置处理器：安全拦截（如文件类型、参数拦截）
     *      3、（@todo）后置处理器：返回参数处理（如统一翻译、脱敏、列权限）
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "controllerPointcut()")
    public Object controllerAop(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        try {
            ControllerHandlerExecutor.executePreHandler(joinPoint);
            result = ControllerHandlerExecutor.executePostHandler(joinPoint, joinPoint.proceed());
            return result;
        } catch (Throwable e) {
            throw e;
        }
    }
}
