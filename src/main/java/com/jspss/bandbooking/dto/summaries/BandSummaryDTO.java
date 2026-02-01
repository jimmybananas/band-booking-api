package com.jspss.bandbooking.dto.summaries;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Summary DTO for band.")
public record BandSummaryDTO(

        @Schema(description = "Band ID", example = "4")
        Long id,

        @Schema(description = "Band name.", example = "Real Eyez")
        String name,

        @Schema(description = "List of musician IDs who belong to the band.")
        List<Long> musicianIds
) {
}
