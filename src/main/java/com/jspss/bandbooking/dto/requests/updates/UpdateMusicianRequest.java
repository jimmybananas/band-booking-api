package com.jspss.bandbooking.dto.requests.updates;

import java.util.List;

public record UpdateMusicianRequest(
        String name,
        String email,
        String phoneNumber,
        List<Long> instrumentIds,
        List<Long> styleIds,
        List<Long> bandIds,
        List<Long> bookingIds
){
}
