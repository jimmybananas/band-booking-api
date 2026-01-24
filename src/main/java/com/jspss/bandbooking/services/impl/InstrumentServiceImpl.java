package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateInstrumentRequest;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.exceptions.BadRequestException;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.InstrumentRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final AuditLogger auditLogger;

    @Override
    public Instrument getInstrument(Long id) {
        return  instrumentRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Instrument not found."));
    }

    @Override
    public List<Instrument> getAllInstruments() {
        return instrumentRepository.findAll();
    }

    @Override
    public Instrument createInstrument(String instrumentName) {
        String cleaned = instrumentName.trim();
        String normalized = normalizeName(cleaned);

        if(instrumentRepository.existsByNameIgnoreCase(normalized)){
            throw new BadRequestException("Instrument already exists: " + cleaned);
        }

        Instrument instrument = new Instrument();
        instrument.setName(capitalize(normalized));
        instrumentRepository.save(instrument);

        auditLogger.log(
                "CREATE_INSTRUMENT",
                "Instrument",
                instrument.getId(),
                "Instrument " + instrument.getName() + " created."

        );

        return instrument;
    }

    @Override
    public List<Instrument> createInstruments(List<String> instrumentsNames) {
        List<Instrument> instrumentList = new ArrayList<>();

        for(String rawName : instrumentsNames) {
            String cleaned = rawName.trim();
            String normalized = normalizeName(cleaned);

            if(instrumentRepository.existsByNameIgnoreCase(normalized)) {
                throw new BadRequestException("Instrument already exists: " + cleaned);
            }

            if(instrumentList.stream().anyMatch(i-> normalizeName(i.getName()).equals(normalized))) {
                throw new BadRequestException("Duplicate instrument in request: " + cleaned);
            }

            Instrument instrument = new Instrument();
            String displayName = capitalize(normalized);
            instrument.setName(displayName);
            instrumentRepository.save(instrument);
            instrumentList.add(instrument);
        }

        auditLogger.log(
                "CREATE_INSTRUMENTS_BULK",
                "INSTRUMENT",
                null,
                "Created " + instrumentList.size() + " instruments " +
                        instrumentList.stream().map(i-> i.getName() +
                                " (id:" + i.getId() + ")").toList()
        );
        return instrumentList;
    }

    @Override
    public List<Instrument> searchInstruments(String query) {
        if( query == null || query.trim().isEmpty()){
            return instrumentRepository.findAll();
        }
        return instrumentRepository.findByNameContainingIgnoreCase(query);

    }

    @Override
    public Instrument updateInstrument(Long id, UpdateInstrumentRequest request) {
        Instrument existing = instrumentRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Instrument not found"));

        String before = existing.toString();
        existing.setName(request.name());
        String after = existing.toString();

        instrumentRepository.save(existing);

        auditLogger.log(
                "INSTRUMENT_UPDATE",
                "Instrument",
                id,
                "Instrument updated from " + before + " to " + after
        );

        return existing;
    }

    @Override
    public void deleteInstrument(Long id) {
        instrumentRepository.deleteById(id);
        auditLogger.log(
                "DELETE_INSTRUMENT",
                "Instrument",
                id,
                "Instrument " + getInstrument(id).getName() + " deleted."
        );
    }

    private String normalizeName(String name){
        return name.trim().toLowerCase();
    }
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
