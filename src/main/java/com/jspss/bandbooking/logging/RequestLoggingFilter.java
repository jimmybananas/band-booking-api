package com.jspss.bandbooking.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String traceId = MDC.get("traceId");
        if(traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        long start = System.currentTimeMillis();

        log.info("Incoming request. method={}, path={}, traceId={}",
                httpReq.getMethod(),
                httpReq.getRequestURI(),
                traceId);

        try {
            filter.doFilter(request, response);
        }finally {
            long duration = System.currentTimeMillis() - start;
            log.info("Completed request. method={}, path={}, status={}, duration={}, traceId={}",
                    httpReq.getMethod(),
                    httpReq.getRequestURI(),
                    httpResp.getStatus(),
                    duration,
                    traceId
            );

            MDC.remove(traceId);
        }

    }
}
