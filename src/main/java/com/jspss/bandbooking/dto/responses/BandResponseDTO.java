package com.jspss.bandbooking.dto.responses;

import java.util.List;

public record BandResponseDTO(
        Long id,
        String name,
        List<Long> musicianIds,
        List<Long> musicStyleIds,
        List<Long> requiredInstrumentIds
) {
}
