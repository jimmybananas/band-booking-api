package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for creating a band.")
public record CreateBandRequest(

        @Schema(
                description = "A string for the name of the new band. Cannot be blank.",
                example = "The Lone Rangers"
        )
        @NotBlank(message ="Band name must not be blank")
        String name
) {}
