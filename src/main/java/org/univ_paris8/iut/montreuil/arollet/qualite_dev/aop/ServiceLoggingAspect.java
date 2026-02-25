package org.univ_paris8.iut.montreuil.arollet.qualite_dev.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("execution(* org.univ_paris8.iut.montreuil.arollet.qualite_dev.service..*(..))")
    public Object aroundServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();
        LOGGER.info("service.enter method={}", signature);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("service.exit method={} durationMs={}", signature, System.currentTimeMillis() - start);
            return result;
        } catch (Exception ex) {
            LOGGER.error("service.error method={} durationMs={} type={} message={}", signature,
                System.currentTimeMillis() - start, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}
