package com.jspss.bandbooking.dto.requests.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String phoneNumber
) {
}
