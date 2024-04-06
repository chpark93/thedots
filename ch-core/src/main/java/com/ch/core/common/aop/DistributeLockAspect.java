package com.ch.core.common.aop;

import com.ch.core.annotation.DistributeLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributeLockAspect {

    private final RedissonClient redissonClient;
    private final TransactionAspect transactionAspect;

    @Around("@annotation(com.ch.core.annotation.DistributeLock)")
    public Object handleDistributeLock(ProceedingJoinPoint joinPoint) throws Throwable {

        // Get annotation
        DistributeLock annotation = getAnnotation(joinPoint);
        // Get lock name and acquire lock
        String lockName = getLockName(annotation.value(), annotation);
        RLock lock = redissonClient.getLock(lockName);

        try {
            if ( lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit()) ) {
                try {
                    log.info("Acquired Lock : {}", lockName);
                    return transactionAspect.proceed(joinPoint);
                } finally {
                    lock.unlock();
                    log.info("Released Lock : {}", lockName);
                }
            } else {
                log.warn("Failed To Acquire Lock : {}", lockName);
                throw new IllegalStateException("Failed To Acquire Lock");
            }

        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted While Acquiring Lock", e);
        }
    }

    private DistributeLock getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(DistributeLock.class);
    }

    private String getLockName(String targetId, DistributeLock annotation) {
        String lockNameFormat = "lock:%s:%s";
        return String.format(lockNameFormat, annotation.lockName(), targetId);
    }

}
