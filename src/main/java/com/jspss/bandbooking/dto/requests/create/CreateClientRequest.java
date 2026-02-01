package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.EnableScheduling;

@Schema(description = "Request body for creating a client.")
public record CreateClientRequest(

        @Schema(
                description = "The name of the client.", example = "Bob Crampet"
        )
        @NotNull()
        String name,

        @Schema(
                description = "The email of the client.", example = "bobcrampet@gmail.com"
        )
        @Email
        String email,

        @Schema(
                description = "Phone number of the client.", example = "555-555-5555"
        )
        @NotNull
        String phoneNumber
) {
}
