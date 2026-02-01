package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request body for adding multiple instruments to a band or musician.")
public record AddInstrumentRequest(

        @Schema(
                description = "List of instrument IDs to add.",
                example = "[3,5,7]"
        )
        List<Long> instrumentIds
) {
}
