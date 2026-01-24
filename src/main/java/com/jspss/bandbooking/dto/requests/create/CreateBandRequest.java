package com.jspss.bandbooking.dto.requests.create;

import jakarta.validation.constraints.NotBlank;

public record CreateBandRequest(
        @NotBlank(message ="Band Name must not be blank")
        String name
) {}
