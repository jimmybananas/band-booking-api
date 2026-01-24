package com.jspss.bandbooking.dto.responses;

import com.jspss.bandbooking.dto.summaries.MusicianSummaryDTO;
import com.jspss.bandbooking.entities.enums.BookingStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record BookingResponseDTO(
        Long id,
        String bandName,
        Long bandId,
        String clientName,
        Long clientId,
        List<MusicianSummaryDTO> musicians,
        ZonedDateTime gigStarts,
        ZonedDateTime gigEnds,
        String city,
        String state,
        BookingStatus status
) {


}
