package com.jspss.bandbooking.mappers;

import com.jspss.bandbooking.dto.responses.MusicStyleResponseDTO;
import com.jspss.bandbooking.entities.MusicStyle;
import org.springframework.stereotype.Component;

@Component
public class MusicStyleMapper {
    public MusicStyleResponseDTO toDTO(MusicStyle musicStyle) {
        return new MusicStyleResponseDTO(
                musicStyle.getId(),
                musicStyle.getName()
        );
    }
}
