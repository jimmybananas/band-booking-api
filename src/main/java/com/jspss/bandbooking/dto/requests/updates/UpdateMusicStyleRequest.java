package com.jspss.bandbooking.dto.requests.updates;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating music style.")
public record UpdateMusicStyleRequest(

        @Schema(
                description = "Name of the music style."
        )
        @NotNull
        String name
) {
}
