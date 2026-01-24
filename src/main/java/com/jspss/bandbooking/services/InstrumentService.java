package com.jspss.bandbooking.services;

import com.jspss.bandbooking.dto.requests.updates.UpdateInstrumentRequest;
import com.jspss.bandbooking.entities.Instrument;

import java.util.List;

public interface InstrumentService {
    Instrument getInstrument(Long id);
    List<Instrument> getAllInstruments();
    Instrument createInstrument(String instrumentName);
    List<Instrument> createInstruments(List<String> instrumentNames);
    List<Instrument> searchInstruments(String query);
    Instrument updateInstrument(Long id, UpdateInstrumentRequest request);
    void deleteInstrument(Long id);
}


