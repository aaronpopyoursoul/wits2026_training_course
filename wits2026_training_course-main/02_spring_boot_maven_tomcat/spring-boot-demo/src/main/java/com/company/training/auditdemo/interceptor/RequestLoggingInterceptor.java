package com.company.training.auditdemo.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;

import com.company.training.auditdemo.filter.RequestLoggingFilter;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        Object traceId = request.getAttribute(RequestLoggingFilter.TRACE_ID_ATTRIBUTE);
        Object routePattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        logger.info("[Interceptor][PRE] traceId={} method={} uri={} route={} handler={}",
            traceId,
            request.getMethod(),
            request.getRequestURI(),
            routePattern,
            resolveHandlerName(handler));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = startTime == null ? -1L : System.currentTimeMillis() - startTime.longValue();
        Object traceId = request.getAttribute(RequestLoggingFilter.TRACE_ID_ATTRIBUTE);
        Object routePattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        logger.info("[Interceptor][POST] traceId={} status={} durationMs={} uri={} route={} handler={} exception={}",
            traceId,
            response.getStatus(),
            duration,
            request.getRequestURI(),
            routePattern,
            resolveHandlerName(handler),
            ex == null ? "-" : ex.getClass().getSimpleName());
    }

    private String resolveHandlerName(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.getBeanType().getSimpleName() + "#" + handlerMethod.getMethod().getName();
        }
        return handler == null ? "-" : handler.getClass().getSimpleName();
    }
}
