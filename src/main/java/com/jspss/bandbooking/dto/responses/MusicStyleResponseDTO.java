package com.jspss.bandbooking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body for music style.")
public record MusicStyleResponseDTO(

        @Schema(description = "Music style ID", example = "45")
        Long id,

        @Schema(description = "Music style name.")
        String name
) {
}
