package com.hsbc.common.aop;

import com.hsbc.common.base.RbcsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnBean(DataSourceAutoConfiguration.class)
public class RbcsManagerAop {
    private final TransactionTemplate transactionTemplate;

    @Pointcut("execution(public * com.hsbc..*.manager..*.* (..)))")
    public void managerPointcut() {
    }

    /**
     * manager层环绕通知
     *      1、（@todo）记录日志
     *      2、（非查询方法）尝试在事务中执行方法。
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "managerPointcut()")
    public Object managerAop(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        try {
            result = tryTransaction(joinPoint);
        } catch (Throwable e) {
            throw e;
        } finally {
            // 避免手工开启事务之后忘记关闭带来的连接池泄露
            TransactionSynchronizationManager.clear();
        }
        return result;
    }

    public Object tryTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isNeedOpenTransaction(joinPoint)) {
            return transactionTemplate.execute(status -> {
                try {
                    log.debug("事务开启");
                    return joinPoint.proceed();
                } catch (RbcsException e) {
                    status.setRollbackOnly();
                    throw e;
                } catch (Throwable e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            });
        }
        return joinPoint.proceed();
    }

    /**
     * 判断方法是否需要开启事务。
     *
     * @param joinPoint 切面的连接点，用于获取方法名。
     * @return 如果方法名以特定操作（如添加、修改、删除、导入）开头，则返回true，表示需要开启事务；否则返回false。
     */
    public boolean isNeedOpenTransaction(ProceedingJoinPoint joinPoint) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            log.debug("【managerAop】事务已存在，无需重复开启");
            return false;
        }
        String methodName = joinPoint.getSignature().getName();
        boolean noTransaction = methodName.startsWith("query")
                || methodName.startsWith("list")
                || methodName.startsWith("get")
                || methodName.startsWith("detail");
        if (noTransaction) {
            log.debug("【managerAop】无需开启事务，methodName：{}", methodName);
        }
        return noTransaction;
    }
}
