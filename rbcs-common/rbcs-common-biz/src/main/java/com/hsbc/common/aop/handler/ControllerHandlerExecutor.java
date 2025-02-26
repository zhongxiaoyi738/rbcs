package com.hsbc.common.aop.handler;

import com.hsbc.common.aop.handler.post.IControllerPostHandler;
import com.hsbc.common.aop.handler.pre.IControllerPreHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import java.util.List;


public class ControllerHandlerExecutor {
    // 储存前置处理程序的列表
    private static List<IControllerPreHandler> preHandlers;

    // 储存后置处理程序的列表
    private static List<IControllerPostHandler> postHandlers;

    public ControllerHandlerExecutor(List<IControllerPreHandler> preHandlers, List<IControllerPostHandler> postHandlers) {
        ControllerHandlerExecutor.preHandlers = preHandlers;
        ControllerHandlerExecutor.postHandlers = postHandlers;
    }

    /**
     * 执行所有的前置处理程序。
     *
     * @param joinPoint AOP的连接点，表示正在执行的方法
     */
    public static void executePreHandler(ProceedingJoinPoint joinPoint) {
        preHandlers.forEach(preHandler -> preHandler.preHandle(joinPoint));
    }

    /**
     * 执行所有的后置处理程序。
     *
     * @param joinPoint AOP的连接点，表示正在执行的方法
     * @param result    方法的原始返回结果
     * @return 经过后置处理程序处理后的结果
     */
    public static Object executePostHandler(ProceedingJoinPoint joinPoint, Object result) {
        Object resultTemp = result;
        for (IControllerPostHandler postHandler : postHandlers) {
            resultTemp = postHandler.postHandle(joinPoint, resultTemp);
        }
        return resultTemp;
    }
}
