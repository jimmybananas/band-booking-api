package com.jspss.bandbooking.services;

import com.jspss.bandbooking.dto.requests.updates.UpdateMusicianRequest;
import com.jspss.bandbooking.entities.Musician;
import jakarta.validation.Valid;

import java.util.List;

public interface MusicianService {
    Musician getMusician(Long id);
    List<Musician> getAllMusicians();
    List<Musician> searchMusicians(String query);
    Musician createMusician(String fullName);
    List<Musician> createMusicians(List<String> fullNameList);
    Musician updateMusician(Long id, @Valid UpdateMusicianRequest request);
    Musician addInstruments(Long id, List<Long> instrumentsIds);
    Musician addMusicStyles(Long id, List<Long> styleIds);
    Musician removeInstruments(Long id, List<Long> instrumentIds);
    Musician removeMusicStyle(Long id, List<Long> styleIds);


}
