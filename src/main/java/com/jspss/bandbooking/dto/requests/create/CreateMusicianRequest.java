package com.jspss.bandbooking.dto.requests.create;

public record CreateMusicianRequest(
        Long id,
        String name,
        String email,
        String phoneNumber
) {

}
