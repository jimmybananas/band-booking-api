package com.jspss.bandbooking.mappers;

import com.jspss.bandbooking.dto.responses.MusicianResponseDTO;
import com.jspss.bandbooking.dto.summaries.MusicianSummaryDTO;
import com.jspss.bandbooking.entities.*;
import org.springframework.stereotype.Component;

@Component
public class MusicianMapper {
    public MusicianResponseDTO toDTO(Musician musician){
        return new MusicianResponseDTO(
                musician.getId(),
                musician.getFullName(),
                musician.getEmail(),
                musician.getPhoneNumber(),
                musician.getCity(),
                musician.getState(),
                musician.getInstruments().stream().map(Instrument::getId).toList(),
                musician.getMusicStyles().stream().map(MusicStyle::getId).toList(),
                musician.getBands().stream().map(Band::getId).toList(),
                musician.getBookingsList().stream().map(Booking::getId).toList()
        );
    }

    public MusicianSummaryDTO toSummary (Musician musician) {
        return new MusicianSummaryDTO(
                musician.getId(),
                musician.getFullName(),
                musician.getInstruments().stream().map(
                        Instrument::getId
                ).toList(),
                musician.getMusicStyles().stream().map(
                        MusicStyle::getId
                ).toList()
        );

    }

}
