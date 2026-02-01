package com.jspss.bandbooking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Response body for Client DTO")
public record ClientResponseDTO(

        @Schema(
                description = "Client ID", example = "34"
        )
        Long id,

        @Schema(
                description = "Client Name", example = "Stephanie Smephanie"
        )
        String name,

        @Schema(
                description = "Client Email", example = "SS@gmail.com"
        )
        @Email
        String email,

        @Schema(
                description = "Client phone number.", example = "555-555-5555"
        )
        String phoneNumber,

        @Schema(
                description = "List of booking IDs", example = "[2,25,55]"
        )
        List<Long> bookingIds
) {
}
