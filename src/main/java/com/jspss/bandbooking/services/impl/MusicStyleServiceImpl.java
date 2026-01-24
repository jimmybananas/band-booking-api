package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateMusicStyleRequest;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.exceptions.BadRequestException;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
import com.jspss.bandbooking.repositories.MusicianRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.MusicStyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicStyleServiceImpl implements MusicStyleService {

    private final MusicStyleRepository musicStyleRepository;
    private final AuditLogger auditLogger;

    @Override
    public MusicStyle getMusicStyle(Long id) {
        return musicStyleRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Music Style not found.")
        );
    }

    @Override
    public List<MusicStyle> getAllMusicStyles() {
        return musicStyleRepository.findAll();
    }

    @Override
    public MusicStyle createMusicStyle(String styleName) {
        String cleaned = styleName.trim();
        String normalized = normalizeName(cleaned);

        if(musicStyleRepository.existsByNameIgnoreCase(normalized))
            throw new BadRequestException("Music style already exists.");

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setName(capitalize(normalized));
        musicStyleRepository.save(musicStyle);

        auditLogger.log(
                "CREATE_MUSIC_STYLE",
                "Music Style",
                musicStyle.getId(),
                "Music Style " + musicStyle.getName() + " created."
        );

        return musicStyle;
    }

    @Override
    public List<MusicStyle> createMusicStyles(List<String> musicStylesList) {
        List<MusicStyle> styles = new ArrayList<>();

        for(String rawName : musicStylesList) {
            String cleaned = rawName.trim();
            String normalized = normalizeName(cleaned);

            if(musicStyleRepository.existsByNameIgnoreCase(normalized)) {
                throw new BadRequestException("Music Style already exists: " + rawName);
            }

            if(styles.stream().anyMatch(s-> normalizeName(s.getName()).equals(normalized))){
                throw new BadRequestException("Duplicate music style in request: " + rawName);
            }
            MusicStyle style = new MusicStyle();
            String displayName = capitalize(normalized);
            style.setName(displayName);
            musicStyleRepository.save(style);
            styles.add(style);
        }

        auditLogger.log(
          "CREATE_MUSIC_STYLES_BULK",
          "Music Style",
          null,
          "Created " + styles.size() + " music Styles " +
                  styles.stream().map(s-> s.getName() +
                          "(id:" + s.getId() + ")").toList()
        );

        return styles;
    }

    @Override
    public List<MusicStyle> searchMusicStyles(String query) {
        if(query == null || query.trim().isEmpty()) {
            return musicStyleRepository.findAll();
        }
        return musicStyleRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public MusicStyle updateMusicStyle(Long id, UpdateMusicStyleRequest request) {
        MusicStyle existing = musicStyleRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Music style not found."));

        String before = existing.toString();

        existing.setName(request.name());

        String after = existing.toString();

        auditLogger.log(
                "UPDATE_MUSIC_STYLE",
                "Music Style",
                id,
                "Music style (ID:" + id + ") updated from " + before + " to " + after
        );
        return musicStyleRepository.save(existing);
    }

    @Override
    public void deleteMusicStyle(Long id) {
        MusicStyle style = musicStyleRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Music style not found"));

        musicStyleRepository.deleteById(id);

        auditLogger.log(
                "DELETE_MUSIC_STYLE",
                "Music Style",
                id,
                "Music Style " + style.getName() + "(ID:" + id + ") deleted."
        );
    }
    private String normalizeName(String name){
        return name.trim().toLowerCase();
    }
    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
