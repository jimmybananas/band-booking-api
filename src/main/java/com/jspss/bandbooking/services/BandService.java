package com.jspss.bandbooking.services;

import com.jspss.bandbooking.dto.requests.updates.UpdateBandRequest;
import com.jspss.bandbooking.entities.Band;

import java.util.List;

public interface BandService {
    Band getBand(Long id);
    List<Band> getAllBands();
    Band createBand(String bandName);
    Band updateBand(Long id, UpdateBandRequest request);
    List<Band> searchBands(String bandName);
    Band addMusician(Long bandId, Long musicianId);
    Band removeMusician(Long bandId, Long musicianId);
    Band addMusicStyle(Long bandId, Long styleId);
    Band removeMusicStyle(Long bandId, Long styleId);
    Band addInstrument(Long bandId, Long instrumentId);
    Band removeInstrument(Long bandId, Long instrumentId);
    void deleteBand(Long id);
}
