package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request body for adding one or more music styles to a band or musician.")
public record AddMusicStyleRequest(

        @Schema(
                description = "List of music style IDs to add.",
                example = "[2,4,6]"
        )
        List<Long> styleIds
) {
}
