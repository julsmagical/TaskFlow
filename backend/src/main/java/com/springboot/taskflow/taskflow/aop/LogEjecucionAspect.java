package com.springboot.taskflow.taskflow.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogEjecucionAspect {

    private static final Logger log = LoggerFactory.getLogger(LogEjecucionAspect.class);

    @Around("@annotation(com.springboot.taskflow.taskflow.aop.LogEjecucion)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        log.info("→ Ejecutando {}.{} con argumentos: {}", className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            log.info("← {}.{} finalizó en {} ms. Resultado: {}", className, methodName, elapsed, result);
            return result;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("✗ {}.{} falló en {} ms. Excepción: {}", className, methodName, elapsed, ex.getMessage());
            throw ex;
        }
    }
}