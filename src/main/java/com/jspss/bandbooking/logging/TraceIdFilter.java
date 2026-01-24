package com.jspss.bandbooking.logging;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.Filter;

@Slf4j
@Component
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        try {
            filterChain.doFilter(request,response);
        } finally {
            MDC.remove(traceId);
        }
    }
}
