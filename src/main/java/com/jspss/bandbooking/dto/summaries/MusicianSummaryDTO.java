package com.jspss.bandbooking.dto.summaries;

import java.util.List;

public record MusicianSummaryDTO(
        Long id,
        String fullName,
        List<Long> instrumentIds,
        List<Long> musicStyleIds
) {
}
