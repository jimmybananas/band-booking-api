package com.jspss.bandbooking.dto.requests.updates;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request body for updating a musician.")
public record UpdateMusicianRequest(

        @Schema(
                description = "Name of the musician.", example = "Big Tom Delegreaux"
        )
        @NotNull
        String name,

        @Schema(
                description = "Email of the musician.", example = "BTD@gmail.com"
        )
        @NotNull
        @Email
        String email,

        @Schema(
                description = "Phone number of the musician."
        )
        @NotNull
        String phoneNumber,

        @Schema(
                description = "List of instrument IDs.", example = "[5, 10, 23]"
        )
        @NotNull
        List<Long> instrumentIds,

        @Schema(
                description = "List of music style IDs.", example = "[5, 10, 23]"
        )
        @NotNull
        List<Long> styleIds,

        @Schema(
                description = "List of band IDs.", example = "[5, 10, 23]"
        )
        @NotNull
        List<Long> bandIds,

        @Schema(
                description = "List of booking IDs.", example = "[5, 10, 23]"
        )
        @NotNull
        List<Long> bookingIds
){
}
