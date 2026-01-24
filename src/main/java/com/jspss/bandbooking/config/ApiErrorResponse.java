package com.jspss.bandbooking.config;

import java.time.ZonedDateTime;

public record ApiErrorResponse(
        String message,
        String error,
        int status,
        String code,
        ZonedDateTime timeStamp,
        String traceId
) {
}
