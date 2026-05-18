package com.company.training.auditdemo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.company.training.auditdemo.filter.RequestLoggingFilter;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingAspect.class);

    @Around("execution(* com.company.training.auditdemo.controller..*(..))")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();
        String traceId = resolveTraceId();

        logger.info("[AOP][IN] traceId={} method={}", traceId, signature);
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("[AOP][OUT] traceId={} method={} durationMs={}", traceId, signature, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[AOP][ERROR] traceId={} method={} durationMs={} message={}",
                traceId,
                signature,
                duration,
                ex.getMessage());
            throw ex;
        }
    }

    private String resolveTraceId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            Object traceId = ((ServletRequestAttributes) requestAttributes)
                .getRequest()
                .getAttribute(RequestLoggingFilter.TRACE_ID_ATTRIBUTE);
            return traceId == null ? "-" : String.valueOf(traceId);
        }
        return "-";
    }
}
