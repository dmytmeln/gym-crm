package com.gym.crm.transaction;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionAspect {

    private final TransactionManager transactionManager;

    @Around("@annotation(com.gym.crm.transaction.Transaction)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) {
        return transactionManager.executeReturningWithinTx(() -> proceedJoinPointWithExceptionHandling(joinPoint));
    }

    private Object proceedJoinPointWithExceptionHandling(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
