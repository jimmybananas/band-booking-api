package com.jspss.bandbooking.dto.requests.create;

import java.util.List;

public record AddMusicStyleRequest(
        List<Long> styleIds
) {
}
