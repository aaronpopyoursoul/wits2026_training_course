package com.company.training.auditdemo.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_ATTRIBUTE = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String REQUEST_RECEIVED_AT_ATTRIBUTE = "requestReceivedAt";

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        long requestReceivedAt = System.currentTimeMillis();

        request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);
        request.setAttribute(REQUEST_RECEIVED_AT_ATTRIBUTE, Long.valueOf(requestReceivedAt));
        response.setHeader(TRACE_ID_HEADER, traceId);
        MDC.put(TRACE_ID_ATTRIBUTE, traceId);

        try {
            logger.info("[Filter][IN] traceId={} method={} uri={} query={} clientIp={} userAgent={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - requestReceivedAt;
            logger.info("[Filter][OUT] traceId={} status={} durationMs={}", traceId, response.getStatus(), duration);
            MDC.remove(TRACE_ID_ATTRIBUTE);
        }
    }
}
