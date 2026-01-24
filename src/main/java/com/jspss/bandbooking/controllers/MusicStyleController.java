package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.requests.create.CreateInstrumentRequest;
import com.jspss.bandbooking.dto.requests.create.CreateMusicStyleRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateMusicStyleRequest;
import com.jspss.bandbooking.dto.responses.MusicStyleResponseDTO;
import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.mappers.MusicStyleMapper;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
import com.jspss.bandbooking.services.MusicStyleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/musicstyles")
@RequiredArgsConstructor
public class MusicStyleController {

    private final MusicStyleService musicStyleService;
    private final MusicStyleMapper musicStyleMapper;

    @GetMapping("/{id}")
    public ResponseEntity<MusicStyleResponseDTO> getMusicStyle (@PathVariable Long id){
        MusicStyle musicStyle = musicStyleService.getMusicStyle(id);
        return ResponseEntity.ok(musicStyleMapper.toDTO(musicStyle));
    }

    @GetMapping
    public ResponseEntity<List<MusicStyleResponseDTO>> getAllMusicStyles(){
        List<MusicStyle> allStyles = musicStyleService.getAllMusicStyles();
        return ResponseEntity.ok(allStyles
                .stream().map(musicStyleMapper::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<MusicStyleResponseDTO> createMusicStyle(@Valid @RequestBody CreateMusicStyleRequest request){
         MusicStyle musicStyle = musicStyleService.createMusicStyle(request.name());
         return ResponseEntity.status(201).body(musicStyleMapper.toDTO(musicStyle));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<MusicStyleResponseDTO>> createMusicStyles(@RequestBody List<CreateMusicStyleRequest> requests){
        List<String> names = requests.stream().map(CreateMusicStyleRequest::name).toList();
        List<MusicStyle> musicStyles = musicStyleService.createMusicStyles(names);
        return ResponseEntity.status(201).body(musicStyles.stream()
               .map(musicStyleMapper::toDTO).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicStyleResponseDTO> updateMusicStyle(@PathVariable Long id, @Valid @RequestBody UpdateMusicStyleRequest request){
        MusicStyle musicStyle =  musicStyleService.updateMusicStyle(id, request);
        return ResponseEntity.ok(musicStyleMapper.toDTO(musicStyle));
    }

    @DeleteMapping("/{id}")
    public void deleteMusicStyle(@PathVariable Long id){
        musicStyleService.deleteMusicStyle(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MusicStyleResponseDTO>> searchMusicStyles(String query) {
        List<MusicStyle> musicStyleList = musicStyleService.searchMusicStyles(query);
        return ResponseEntity.ok(musicStyleList
                .stream().map(musicStyleMapper::toDTO).toList());
    }
}
