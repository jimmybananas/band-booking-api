package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for the creation of a new musician.")
public record CreateMusicianRequest(

        @Schema(
                description = "Name of the musician.", example = "Elton John"
        )
        @NotNull
        String name,

        @Schema(
                description =
                        "The email of the new musician.", example = "eltonjohn@email.com"
        )
        @Email
        String email,

        @Schema(
                description = "Phone number of the new musician.", example = "555-555-5555"
        )
        @NotNull
        String phoneNumber
) {

}
