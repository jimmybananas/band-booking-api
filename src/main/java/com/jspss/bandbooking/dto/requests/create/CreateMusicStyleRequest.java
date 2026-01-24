package com.jspss.bandbooking.dto.requests.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateMusicStyleRequest(
        @NotBlank(message = "Style list cannot be blank.")
        String name
) {
}
