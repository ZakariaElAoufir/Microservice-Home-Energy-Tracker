package com.leetjourney.user_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Pointcut("execution(* com.leetjourney.user_service.service.*.*(..))")
    public void serviceMethod(){}

    @Before("serviceMethod()")
    public void logBefore(JoinPoint joinPoint){
        log.info("Called service method: {} with arguments: {}", 
                            joinPoint.getSignature().getName(), 
                            joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethod()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result){
        log.info("service method: {}, returned: {}", 
                            joinPoint.getSignature().getName(), 
                            result);
    }

}
