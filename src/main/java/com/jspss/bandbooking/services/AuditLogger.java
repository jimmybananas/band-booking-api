package com.jspss.bandbooking.services;

import com.jspss.bandbooking.entities.AuditLog;
import com.jspss.bandbooking.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogger {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entityType, Long entityId, String details){
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setTraceId(MDC.get("traceId"));
        log.setTimeStamp(ZonedDateTime.now());

        auditLogRepository.save(log);
    }
}
