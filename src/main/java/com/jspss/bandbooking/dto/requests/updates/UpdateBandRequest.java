package com.jspss.bandbooking.dto.requests.updates;

import java.util.List;

public record UpdateBandRequest(
        String name,
        List<Long> musicStyleIdList,
        List<Long> musicianIdList,
        List<Long> requiredInstrumentsIdList
) {
}
