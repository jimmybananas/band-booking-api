package com.jspss.bandbooking.mappers;

import com.jspss.bandbooking.dto.responses.BandResponseDTO;
import com.jspss.bandbooking.dto.summaries.BandSummaryDTO;
import com.jspss.bandbooking.entities.Band;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.entities.Musician;
import org.springframework.stereotype.Component;

@Component
public class BandMapper {

    public BandResponseDTO toDTO(Band band){
        return new BandResponseDTO(
                band.getId(),
                band.getBandName(),
                band.getBandMembers().stream()
                        .map(Musician::getId).toList(),
                band.getMusicStyles().stream()
                        .map(MusicStyle::getId).toList(),
                band.getRequiredInstruments().stream().
                        map(Instrument::getId).toList()
        );
    }

    public BandSummaryDTO toSummaryDTO(Band band){
        return new BandSummaryDTO(
                band.getId(),
                band.getBandName(),
                band.getBandMembers().stream().map(Musician::getId).toList()
        );
    }
}
