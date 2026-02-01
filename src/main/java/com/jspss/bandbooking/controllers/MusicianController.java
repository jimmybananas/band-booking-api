package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.requests.create.CreateMusicianRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateMusicianRequest;
import com.jspss.bandbooking.dto.responses.MusicianResponseDTO;
import com.jspss.bandbooking.dto.summaries.MusicianSummaryDTO;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.Musician;
import com.jspss.bandbooking.mappers.MusicianMapper;
import com.jspss.bandbooking.repositories.AuditLogRepository;
import com.jspss.bandbooking.services.MusicianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final AuditLogRepository auditLogRepository;

    @Operation(summary = "Get musician by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Musician found."),
            @ApiResponse(responseCode = "404", description = "Could not find Musician.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MusicianResponseDTO> getMusician(@PathVariable Long id){
        Musician musician = musicianService.getMusician(id);
        return ResponseEntity.ok(musicianMapper.toDTO(musician));
    }

    @Operation(summary = "Get all musicians.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All musicians found.")
    })
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

    @Operation(summary = "Create a musician.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Musician successfully created."),
            @ApiResponse(responseCode = "400", description = "Could not create musician.")
    })
    @PostMapping
    public ResponseEntity<MusicianResponseDTO> createMusician(@Valid @RequestBody CreateMusicianRequest request){
        Musician musician =  musicianService.createMusician(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(musicianMapper.toDTO(musician));
    }

    @Operation(summary = "Create a list of musicians.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created a list of musicians."),
            @ApiResponse(responseCode = "400", description = "Could not create all musicians.")
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<MusicianResponseDTO>> createMusicians(@Valid @RequestBody List<CreateMusicianRequest> requests) {
        List<String> musicianNames = requests.stream().map(CreateMusicianRequest::name).toList();
        List<Musician> musicianList = musicianService.createMusicians(musicianNames);
        return ResponseEntity.status(HttpStatus.CREATED).body(musicianList
                .stream().map(musicianMapper::toDTO).toList());
    }

    @Operation(summary = "Update a musician by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the musician."),
            @ApiResponse(responseCode = "400", description = "Could not update the musician."),
            @ApiResponse(responseCode = "404", description = "Musician could not be found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MusicianSummaryDTO> updateMusician(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateMusicianRequest request){
        Musician musician =  musicianService.updateMusician(id, request);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @Operation(summary = "Add an instrument to a musician's instrument list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully added instrument to the musical's instrument list."),
            @ApiResponse(responseCode = "400", description = "Could not add instrument to musician's instrument list."),
            @ApiResponse(responseCode = "404", description = "Musician and/or instrument not found.")
    })
    @PatchMapping("/{id}/instruments")
    public ResponseEntity<MusicianSummaryDTO> addInstruments (@PathVariable Long id, @Valid @RequestBody List<Long> instrumentIds) {
        Musician musician = musicianService.addInstruments(id,instrumentIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @Operation(summary = "Remove an instrument from a musician's instrument list.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully removed instrument from the musical's instrument list."),
            @ApiResponse(responseCode = "400", description = "Could not remove instrument from musician's instrument list."),
            @ApiResponse(responseCode = "404", description = "Musician and/or instrument not found.")
    })

    @DeleteMapping("/{id}/instruments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MusicianSummaryDTO> removeInstruments(@PathVariable Long id,
                                                                      @Valid @ RequestBody List<Long> instrumentIds) {
        Musician musician = musicianService.removeInstruments(id, instrumentIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @Operation(summary = "Add a style to a musician's music style list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully added music style to the musician's music style list."),
            @ApiResponse(responseCode = "400", description = "Could not add music style to musician's music style list."),
            @ApiResponse(responseCode = "404", description = "Musician and/or music style not found.")
    })
    @PatchMapping("/{id}/musicstyles")
    public ResponseEntity<MusicianSummaryDTO> addStyles(@PathVariable Long id,
                                                        @Valid @RequestBody List<Long> styleIds){
        Musician musician = musicianService.addMusicStyles(id, styleIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }

    @Operation(summary = "Remove a style from a musician's music style list.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully removed music style from the musician's music style list."),
            @ApiResponse(responseCode = "400", description = "Could not remove music style from musician's music style list."),
            @ApiResponse(responseCode = "404", description = "Musician and/or music style not found.")
    })

    @DeleteMapping("/{id}/musicstyles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MusicianSummaryDTO> removeStyles(@PathVariable Long id, @RequestBody List<Long> styleIds) {
        Musician musician = musicianService.removeMusicStyle(id, styleIds);
        return ResponseEntity.ok(musicianMapper.toSummary(musician));
    }


}
