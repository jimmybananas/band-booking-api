package com.jspss.bandbooking.dto.summaries;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Summary DTO for musician")
public record MusicianSummaryDTO(

        @Schema(description = "Musician ID", example = "12")
        Long id,

        @Schema(description = "Musician full name.", example = "Roxo the Rock and Roll Clown")
        String fullName,

        @Schema(description = "List of instrument IDs", example = "[2,10,34]")
        List<Long> instrumentIds,

        @Schema(description = "List of music style IDs", example = "[2,10,34]")
        List<Long> musicStyleIds
) {
}
