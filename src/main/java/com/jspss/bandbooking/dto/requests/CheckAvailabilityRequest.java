package com.jspss.bandbooking.dto.requests;

import java.time.ZonedDateTime;

public record CheckAvailabilityRequest(
        Long bandId,
        ZonedDateTime start,
        ZonedDateTime end
) {
}
