package com.jspss.bandbooking.dto.summaries;

import java.util.List;

public record BandSummaryDTO(
        Long id,
        String name,
        List<Long> musicianIds
) {
}
