package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateMusicianRequest;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.entities.Musician;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.InstrumentRepository;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
import com.jspss.bandbooking.repositories.MusicianRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.MusicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicianServiceImpl implements MusicianService {

    private final MusicianRepository musicianRepository;
    private final InstrumentRepository instrumentRepository;
    private final MusicStyleRepository musicStyleRepository;
    private final AuditLogger auditLogger;

    @Override
    public Musician getMusician(Long id) {
        return musicianRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Musician not found.")
        );
    }

    @Override
    public List<Musician> getAllMusicians() {
        return musicianRepository.findAll();
    }

    @Override
    public List<Musician> searchMusicians(String query) {
        return musicianRepository.fuzzySearch(query);
    }

    @Override
    public List<Musician> createMusicians(List<String> musicians) {
        List<Musician> musicianList = new ArrayList<>();

        for(String name: musicians) {
            Musician musician = createMusician(name);
            musicianList.add(musician);
        }

        auditLogger.log(
                "MUSICIANS_CREATED_BUILK",
                "Musician",
                null,
                "Created " + musicianList.size() + "musicians " +
                        musicianList.stream().map(m-> m.getFullName() +
                                ("Id:" + m.getId() + ")")).toList()
        );

        return musicianList;
    }

    @Override
    public Musician createMusician(String musicianName) {
        Musician musician = new Musician();
        musician.setFullName(musicianName);

        musicianRepository.save(musician);

        auditLogger.log(
                "CREATE_MUSICIAN",
                "Musician",
                musician.getId(),
                "Musician created: " + musician.toString()
        );

        return musician;
    }

    @Override
    public Musician updateMusician(Long id, @Valid UpdateMusicianRequest reqeust) {
        Musician existing = musicianRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Musician not found."));

        String before  = existing.toString();

        musicianRepository.save(existing);

        String after = existing.toString();

        auditLogger.log(
                "UPDATE_MUSICIAN",
                "Musician",
                id,
                "Musician ID:" + id + " updated from " + before + " to " + after
        );

        return existing;
    }

    @Override
    public Musician addInstruments(Long id, List<Long> instrumentsIds) {
        Musician musician = musicianRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Musician not found"));
        List<Instrument> instrumentList = instrumentRepository.findAllById(instrumentsIds);

        for(Instrument instrument: instrumentList) {
            if(!musician.getInstruments().contains(instrument))
                musician.getInstruments().add(instrument);
        }
        return musicianRepository.save(musician);
    }

    @Override
    public Musician removeInstruments(Long id, List<Long> instrumentIds) {
        Musician musician = musicianRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Musician not found"));
        List<Instrument> instrumentList = instrumentRepository.findAllById(instrumentIds);
        for(Instrument instrument: instrumentList){
            musician.getInstruments().remove(instrument);
        }
        return musicianRepository.save(musician);
    }

    @Override
    public Musician addMusicStyles(Long id, List<Long> styleIds) {
        Musician musician = musicianRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Musician not found"));
        List<MusicStyle> musicStyleList = musicStyleRepository.findAllById(styleIds);

        for(MusicStyle style: musicStyleList) {
            if(!musician.getMusicStyles().contains(style))
                musician.getMusicStyles().add(style);
        }

        return musicianRepository.save(musician);
    }

    @Override
    public Musician removeMusicStyle(Long id, List<Long> styleIds) {
        Musician musician = musicianRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Musician not Found"));
        List<MusicStyle> styleList = musicStyleRepository.findAllById(styleIds);
        for(MusicStyle style: styleList){
            musician.getMusicStyles().remove(style);
        }
        return musicianRepository.save(musician);
    }


}
