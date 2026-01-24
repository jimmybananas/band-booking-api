package com.jspss.bandbooking.dto.responses;

import java.util.List;

public record MusicianResponseDTO(
        Long id,
        String fullName,
        String phoneNumber,
        String email,
        String city,
        String state,
        List<Long> instrumentIds,
        List<Long> musicStyleIds,
        List<Long> bandIds,
        List<Long> bookingIds
) {
}
