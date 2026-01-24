package com.jspss.bandbooking.dto.responses;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ClientResponseDTO(
        @NotBlank Long id,
        @NotBlank String name,
        @Email String email,
        @NotBlank String phoneNumber,
        List<Long> bookingIds
) {
}
