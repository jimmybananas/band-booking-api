package com.jspss.bandbooking.services;

import com.jspss.bandbooking.dto.requests.create.CreateMusicStyleRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateMusicStyleRequest;
import com.jspss.bandbooking.entities.MusicStyle;

import java.util.List;

public interface MusicStyleService {
    MusicStyle getMusicStyle (Long id);
    List<MusicStyle> getAllMusicStyles();
    MusicStyle createMusicStyle(String name);
    List<MusicStyle> createMusicStyles(List<String> names);
    List<MusicStyle> searchMusicStyles(String query);
    MusicStyle updateMusicStyle(Long id, UpdateMusicStyleRequest request);
    void deleteMusicStyle(Long id);
}
