package com.jspss.bandbooking.dto.requests.create;

import jakarta.validation.constraints.NotBlank;

public record CreateInstrumentRequest(
        @NotBlank
        String name
) {
}
