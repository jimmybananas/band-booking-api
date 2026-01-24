package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.requests.create.CreateMusicianRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateMusicianRequest;
import com.jspss.bandbooking.dto.responses.MusicianResponseDTO;
import com.jspss.bandbooking.dto.summaries.MusicianSummaryDTO;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.Musician;
import com.jspss.bandbooking.mappers.MusicianMapper;
import com.jspss.bandbooking.services.MusicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/musicians")
@RequiredArgsConstructor
public class MusicianController {

    private final MusicianService musicianService;
    private final MusicianMapper musicianMapper;

    @GetMapping("/{id}")
    public ResponseEntity<MusicianResponseDTO> getMusician(@PathVariable Long id){
        Musician musician = musicianService.getMusician(id);
        return ResponseEntity.ok(musicianMapper.toDTO(musician));
    }

    @GetMapping
    public ResponseEntity<List<MusicianSummaryDTO>> getAllMusicians(){
        List<Musician> musicianList = musicianService.getAllMusicians();
        return ResponseEntity.ok(musicianList.stream()
                .map(musicianMapper::toSummary).toList());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MusicianSummaryDTO>> searchMusician(@RequestParam String query) {
        List<Musician> results = musicianService.searchMusicians(query);
        return ResponseEntity.ok(results.stream()
                .map(musicianMapper::toSummary).toList());
    }

    @PostMapping
    public ResponseEntity<MusicianResponseDTO> createMusician(@Valid @RequestBody CreateMusicianRequest request){
        Musician musician =  musicianService.createMusician(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(musicianMapper.toDTO(musician));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<MusicianResponseDTO>> createMusicians(@Valid @RequestBody List<CreateMusicianRequest> requests) {
        List<String> musicianNames = requests.stream().map(CreateMusicianRequest::name).toList();
        List<Musician> musicianList = musicianService.createMusicians(musicianNames);
        return ResponseEntity.status(HttpStatus.CREATED).body(musicianList
                .stream().map(musicianMapper::toDTO).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MusicianSummaryDTO> updateMusician(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateMusicianRequest request){
        Musician musician =  musicianService.updateMusician(id, request);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @PatchMapping("/{id}/instruments")
    public ResponseEntity<MusicianSummaryDTO> addInstruments (@PathVariable Long id, @Valid @RequestBody List<Long> instrumentIds) {
        Musician musician = musicianService.addInstruments(id,instrumentIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @DeleteMapping("/{id}/instruments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MusicianSummaryDTO> removeInstruments(@PathVariable Long id,
                                                                      @Valid @ RequestBody List<Long> instrumentIds) {
        Musician musician = musicianService.removeInstruments(id, instrumentIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @PatchMapping("/{id}/musicstyles")
    public ResponseEntity<MusicianSummaryDTO> addStyles(@PathVariable Long id,
                                                        @Valid @RequestBody List<Long> styleIds){
        Musician musician = musicianService.addMusicStyles(id, styleIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @DeleteMapping("/{id}/musicstyles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MusicianSummaryDTO> removeStyles(@PathVariable Long id, @RequestBody List<Long> styleIds) {
        Musician musician = musicianService.removeMusicStyle(id, styleIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMusician(@PathVariable Long id){
        musicianService.deleteMusician(id);
    }
}
