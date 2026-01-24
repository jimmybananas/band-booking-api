package com.jspss.bandbooking.mappers;

import com.jspss.bandbooking.dto.responses.InstrumentResponseDTO;
import com.jspss.bandbooking.entities.Instrument;
import org.springframework.stereotype.Component;

@Component
public class InstrumentMapper {
    public InstrumentResponseDTO toDTO(Instrument instrument){
        return new InstrumentResponseDTO(
                instrument.getId(),
                instrument.getName()
        );
    }

}
