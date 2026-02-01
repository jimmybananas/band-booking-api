package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Request body for the creation of a new music style.")
public record CreateMusicStyleRequest(

        @Schema(
                description = "Name of the music style."
        )
        @NotBlank(message = "Style list cannot be blank.")
        String name
) {
}
