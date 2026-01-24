package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateBandRequest;
import com.jspss.bandbooking.dto.responses.BandResponseDTO;
import com.jspss.bandbooking.entities.Band;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.entities.Musician;
import com.jspss.bandbooking.exceptions.BadRequestException;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.BandRepository;
import com.jspss.bandbooking.repositories.InstrumentRepository;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
import com.jspss.bandbooking.repositories.MusicianRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.BandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.MissingResourceException;

@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService {

    private final BandRepository bandRepository;
    private final MusicStyleRepository musicStyleRepository;
    private final MusicianRepository musicianRepository;
    private final InstrumentRepository instrumentRepository;
    private final AuditLogger auditLogger;

    @Override
    public Band getBand(Long id) {
        return bandRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Band not found"));
    }

    @Override
    public List<Band> getAllBands() {
        return bandRepository.findAll();
    }

    @Override
    public Band createBand(String bandName) {
        String cleaned = bandName.trim();
        String normalized = normalizeName(cleaned);

        if(bandRepository.existsByNameIgnoreCase(normalized))
            throw new BadRequestException("A band with this name already exists: " + cleaned);
        Band band = new Band();
        band.setBandName(capitalize(normalized));
        bandRepository.save(band);

        auditLogger.log(
                "CREATE_BAND",
                "Band",
                band.getId(),
                "Created band: " +band.getBandName()
        );

        return band;
    }

    @Override
    public Band updateBand(Long id, UpdateBandRequest request) {
        Band band = bandRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Band not found"));

        String before = band.toString();

        if(request.name() != null)
            band.setBandName(request.name());

        List<Musician> lineUp = request.musicianIdList().stream()
                .map(musicianId -> musicianRepository.findById(musicianId)
                        .orElseThrow(()-> new RuntimeException("Musician Not found: ID(" + musicianId + ")"))).toList();
        band.setBandMembers(lineUp);

        List<MusicStyle> musicStyles = request.musicStyleIdList().stream()
                .map(styleId -> musicStyleRepository
                        .findById(styleId)
                        .orElseThrow(()-> new RuntimeException("Music Style not found: ID(" + styleId + ")")))
                .toList();
        band.setMusicStyles(musicStyles);

        List<Instrument> requiredInstruments = request.requiredInstrumentsIdList()
                .stream().map(instrId -> instrumentRepository
                        .findById(instrId)
                        .orElseThrow(()-> new RuntimeException("Instrument not found: ID(" + instrId +")")))
                .toList();
        band.setRequiredInstruments(requiredInstruments);

        validateStructure(musicStyles, lineUp, requiredInstruments);
        validateStyleCompatibility(musicStyles, lineUp);
        validateInstrumentCoverage(requiredInstruments, lineUp);
        bandRepository.save(band);

        String after = band.toString();

        auditLogger.log(
                "UPDATE_BAND",
                "Band",
                id,
                "Before: " + before + " | After: " + after
        );
        return band;
    }

    @Override
    public List<Band> searchBands(String query) {
        if(query == null || query.trim().isEmpty()) {
            return bandRepository.findAll();
        }
        return bandRepository.findByBandNameContainingIgnoreCase(query);
    }

    @Override
    public Band addMusician(Long bandId, Long musicianId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(()-> new RuntimeException("Band not found"));
        Musician musicianToAdd = musicianRepository.findById(musicianId)
                .orElseThrow(()-> new RuntimeException("Musician not found."));

        if (band.getBandMembers().contains(musicianToAdd)) {
            throw new BadRequestException("Musician is already a member of this band");
        }

        band.getBandMembers().add(musicianToAdd);

        bandRepository.save(band);

        auditLogger.log(
                "ADD_MUSICIAN",
                "Band",
                bandId,
                "Musician ID:" + musicianToAdd.getId() + " added to Band ID(" + bandId + ")"
        );

        return band;
    }

    @Override
    public Band removeMusician(Long bandId, Long musicianId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(()-> new NotFoundException("Band Not Found"));
        Musician musicianToRemove = musicianRepository.findById(musicianId)
                .orElseThrow(()-> new NotFoundException("Musician not found."));

        band.getBandMembers().remove(musicianToRemove);

        bandRepository.save(band);

        auditLogger.log(
                "REMOVE_MUSICIAN",
                "Band",
                bandId,
                "Musician (" + musicianToRemove.getFullName() + ") removed."
        );

        return band;
    }

    @Override
    public Band addMusicStyle(Long bandId, Long styleId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(()-> new NotFoundException("band not found"));

        MusicStyle style = musicStyleRepository.findById(styleId)
                .orElseThrow(()-> new NotFoundException("Music style not Found"));

        if (band.getMusicStyles().contains(style)) {
            throw new BadRequestException("Band already has this music style");
        }

        band.getMusicStyles().add(style);

        bandRepository.save(band);

        auditLogger.log(
                "MUSIC_STYLE_ADDED",
                "Band",
                bandId,
                "Music Style Added: " + style.getName()
        );

        return band;
    }

    @Override
    public Band removeMusicStyle(Long bandId, Long styleId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(()-> new NotFoundException("band not found"));

        MusicStyle style = musicStyleRepository.findById(styleId)
                .orElseThrow(()-> new NotFoundException("Music style not Found"));

        band.getMusicStyles().remove(style);

        bandRepository.save(band);

        auditLogger.log(
                "MUSIC_STYLE_REMOVED",
                "Band",
                bandId,
                "Music Style removed: " + style.getName()
        );

        return band;
    }

    @Override
    public Band addInstrument(Long bandId, Long instrumentId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(()-> new NotFoundException("band not found"));

        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(()-> new NotFoundException("Instrument not Found"));

        if (band.getRequiredInstruments().contains(instrument)) {
            throw new BadRequestException("Instrument is already required for this band");
        }

        band.getRequiredInstruments().add(instrument);
        bandRepository.save(band);

        auditLogger.log(
                "INSTRUMENT_ADDED",
                "Band",
                bandId,
                "Instrument Added: " + instrument.getName()
        );

        return band;
    }

    @Override
    public Band removeInstrument(Long bandId, Long instrumentId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(()-> new NotFoundException("band not found"));

        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(()-> new NotFoundException("Instrument not Found"));

        band.getRequiredInstruments().remove(instrument);

        bandRepository.save(band);

        auditLogger.log(
                "INSTRUMENT_REMOVED",
                "Band",
                bandId,
                "Instrument Removed: " + instrument.getName()
        );

        return band;
    }

    @Override
    public void deleteBand(Long id) {
        bandRepository.deleteById(id);

        auditLogger.log(
                "DELETE_BAND",
                "Band",
                id,
                "Deleting Band Id: " + id
        );
    }

    private void validateStyleCompatibility(List<MusicStyle> musicStyles, List<Musician> lineUp) {
        for (Musician musician: lineUp) {
            boolean matches = musician.getMusicStyles().stream()
                    .anyMatch(musicStyles::contains);
            if(!matches) {
                throw new RuntimeException("Musician doesn't play the required style: " + musician.getFullName());
            }
        }
    }

    private void validateInstrumentCoverage(List<Instrument> instruments, List<Musician> lineUp) {
        for (Instrument required: instruments) {
            boolean covered = lineUp.stream()
                    .anyMatch(m-> m.getInstruments().contains(required));

            if(!covered) {
                throw new RuntimeException("Required Instrument not covered by any band members: " + required.getName());
            }
        }
    }

    private void validateStructure(List<MusicStyle> musicStyles, List<Musician> lineUp, List<Instrument> instruments) {
        if (musicStyles.isEmpty()) {
            throw new BadRequestException("A band must have at least one music style");
        }

        if (instruments.isEmpty()) {
            throw new RuntimeException("A band must require at least one instrument");
        }

        if (lineUp.isEmpty()) {
            throw new BadRequestException("A band must have at least one member");
        }
    }

    private List<Instrument> resolveInstruments(List<Instrument> requiredInstruments) {
        return requiredInstruments.stream().map(instrument-> instrumentRepository.findById(instrument.getId())
                .orElseThrow(()-> new NotFoundException("Instrument not found"))).toList();
    }

    private List<Musician> resolveLineUp(List<Musician> bandMembers) {
        return bandMembers.stream().map(member-> musicianRepository.findById(member.getId())
                .orElseThrow(()-> new NotFoundException("Musician not found"))).toList();
    }

    private List<MusicStyle> resolveStyles(List<MusicStyle> musicStyles) {
        return musicStyles.stream()
                .map(style-> musicStyleRepository.findById(style.getId())
                        .orElseThrow(()->new NotFoundException("Music Style not found: " + style.getId()))).toList();
    }
    private String normalizeName(String name){
        return name.trim().toLowerCase();
    }
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
