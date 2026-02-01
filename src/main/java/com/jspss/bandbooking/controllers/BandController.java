package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.responses.BandResponseDTO;
import com.jspss.bandbooking.dto.requests.create.CreateBandRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateBandRequest;
import com.jspss.bandbooking.dto.summaries.BandSummaryDTO;
import com.jspss.bandbooking.entities.Band;
import com.jspss.bandbooking.mappers.BandMapper;
import com.jspss.bandbooking.repositories.*;
import com.jspss.bandbooking.services.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;
    private final BandMapper bandMapper;
    private final MusicStyleRepository musicStyleRepository;
    private final MusicianRepository musicianRepository;
    private final InstrumentRepository instrumentRepository;
    private final ClientRepository clientRepository;
    private final AuditLogRepository auditLogRepository;

    @Operation(summary = "Get a band by Id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Band found."),
            @ApiResponse(responseCode = "404", description = "Band cannot be found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BandResponseDTO> getBand(@PathVariable Long id) {
        Band band = bandService.getBand(id);
        return ResponseEntity.ok(bandMapper.toDTO(band));
    }

    @GetMapping
    public ResponseEntity<List<BandSummaryDTO>> getAllBands() {
        return ResponseEntity.ok(
                bandService.getAllBands()
                        .stream()
                        .map(bandMapper::toSummaryDTO).toList());
    }

    @Operation(summary = "Create a new band.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Band successfully created."),
            @ApiResponse(responseCode = "400", description = "Band could not be created.")
    })
    @PostMapping
    public ResponseEntity<BandResponseDTO> createBand(@Valid @RequestBody CreateBandRequest request) {
        Band band = bandService.createBand(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(bandMapper.toDTO(band));
    }

    @Operation(summary = "Update a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Band successfully updated."),
            @ApiResponse(responseCode = "400", description = "Band could not be updated."),
            @ApiResponse(responseCode = "404", description = "Band could not be found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BandResponseDTO> updateBand(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateBandRequest request) {
        Band band = bandService.updateBand(id, request);
        return ResponseEntity.ok(bandMapper.toDTO(band));
    }

    @Operation(summary = "Delete a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Band deleted."),
            @ApiResponse(responseCode = "400", description = "Could not delete band."),
            @ApiResponse(responseCode = "404", description = "Band could not be found.")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBand(@PathVariable Long id) {
        bandService.deleteBand(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BandResponseDTO>> searchBands(@RequestParam String query) {
        List<Band> bandList = bandService.searchBands(query);
        return ResponseEntity.ok(bandList.stream()
                .map(bandMapper::toDTO).toList());
    }

    @Operation(summary = "Add a musician to a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Musician added."),
            @ApiResponse(responseCode = "400", description = "Could not add musician"),
            @ApiResponse(responseCode = "404", description = "Band or musician not found")
    })
    @PatchMapping("/{bandId}/musicians")
    public ResponseEntity<BandSummaryDTO> addMusician(@PathVariable Long bandId, @RequestParam Long musicianId) {
        Band band = bandService.addMusician(bandId, musicianId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @Operation(summary = "Remove a musician from a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Musician successfully removed."),
            @ApiResponse(responseCode = "400", description = "Musician could not be removed."),
            @ApiResponse(responseCode = "404", description = "Musician or band could not be found.")
    })
    @DeleteMapping("/{bandId}/musicians")
    public ResponseEntity<BandSummaryDTO> removeMusician(@PathVariable Long bandId, @RequestParam Long musicianId) {
        Band band = bandService.removeMusician(bandId, musicianId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @Operation(summary = "Add music style to band.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Musician successfully added."),
            @ApiResponse(responseCode = "400", description = "Musician could not be added."),
            @ApiResponse(responseCode = "404", description = "Musician or band could not be found.")
    })
    @PatchMapping("/{bandId}/musicstyles")
    public ResponseEntity<BandSummaryDTO> addMusicStyle(@PathVariable Long bandId, @RequestParam Long styleId) {
        Band band = bandService.addMusicStyle(bandId, styleId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @Operation(summary = "Remove a music style from a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Music style successfully removed."),
            @ApiResponse(responseCode = "400", description = "Music style could not be removed"),
            @ApiResponse(responseCode = "404", description = "Music style or band could not be found.")
    })
    @DeleteMapping("/{bandId}/musicstyles")
    public ResponseEntity<BandSummaryDTO> removeMusicStyle(@PathVariable Long bandId, @RequestParam Long styleId) {
        Band band = bandService.removeMusicStyle(bandId, styleId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @Operation(summary = "Add a required instrument to a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instrument successfully added."),
            @ApiResponse(responseCode = "400", description = "Instrument could not be added."),
            @ApiResponse(responseCode = "404", description = "Instrument or band not found.")
    })
    @PatchMapping("/{bandId}/instruments")
    public ResponseEntity<BandSummaryDTO> addInstrument(@PathVariable Long bandId, @RequestParam Long instrumentId) {
        Band band = bandService.addInstrument(bandId, instrumentId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @Operation(summary = "Remove a required instruments from a band.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully removed instrument."),
            @ApiResponse(responseCode = "400", description = "Could not removed instrument."),
            @ApiResponse(responseCode = "404", description = "Band or instrument could not be found.")
    })
    @DeleteMapping("/{bandId}/instruments")
    public ResponseEntity<BandSummaryDTO> removeInstrument(@PathVariable Long bandId, @RequestParam Long instrumentId) {
        Band band = bandService.removeInstrument(bandId, instrumentId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

}


