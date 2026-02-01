package com.jspss.bandbooking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response body for Musician DTO.")
public record MusicianResponseDTO(

        @Schema(description = "Musician ID", example = "34")
        Long id,

        @Schema(description = "Musician full name.", example = "Dirk Lance")
        String fullName,

        @Schema(description = "Musicians phone number.", example = "555-555-5555")
        String phoneNumber,

        @Schema(description = "Musician's email.", example = "Dirk_Lance@gmail.com")
        String email,

        @Schema(description = "Musician' city.", example = "Long Beach")
        String city,

        @Schema(description = "Musician's state.", example = "California")
        String state,

        @Schema(description = "List of instrument IDs for musician.", example = "[2,4,6]")
        List<Long> instrumentIds,

        @Schema(description = "List of music style IDs for musician.", example = "[2,4,6]")
        List<Long> musicStyleIds,

        @Schema(description = "List of band IDs for musician.", example = "[2,4,6]")
        List<Long> bandIds,

        @Schema(description = "List of booking IDs for musician.", example = "[2,4,6]")
        List<Long> bookingIds
) {
}
