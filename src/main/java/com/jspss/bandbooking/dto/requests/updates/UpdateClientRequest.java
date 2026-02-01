package com.jspss.bandbooking.dto.requests.updates;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body to update a Client.")
public record UpdateClientRequest(

        @Schema(
                description = "Name of the client.", example = "Elton Jonathan"
        )
        @NotBlank(message = "Name is required")
        String name,

        @Schema(
                description = "Email of the client.", example = "eltyJJboy@ss.com"
        )
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(
                description = "Phone number of the client.", example = "555-555-5555"
        )
        @NotBlank(message =  "Phone Number is required")
        String phoneNumber
) {
}
