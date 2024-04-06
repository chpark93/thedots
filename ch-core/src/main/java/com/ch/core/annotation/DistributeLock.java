package com.ch.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributeLock {

    // value
    String value();
    // lockName
    String lockName();
    // Lock 획득 시도 최대 시간
    long waitTime() default 5L;
    // Lock 획득 후, 점유 최대 시간
    long leaseTime() default 3L;
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
