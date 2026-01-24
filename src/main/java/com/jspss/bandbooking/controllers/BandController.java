package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.responses.BandResponseDTO;
import com.jspss.bandbooking.dto.requests.create.CreateBandRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateBandRequest;
import com.jspss.bandbooking.dto.summaries.BandSummaryDTO;
import com.jspss.bandbooking.entities.Band;
import com.jspss.bandbooking.mappers.BandMapper;
import com.jspss.bandbooking.services.BandService;
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

    @PostMapping
    public ResponseEntity<BandResponseDTO> createBand(@Valid @RequestBody CreateBandRequest request) {
        Band band = bandService.createBand(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(bandMapper.toDTO(band));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BandResponseDTO> updateBand(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateBandRequest request) {
        Band band = bandService.updateBand(id, request);
        return ResponseEntity.ok(bandMapper.toDTO(band));
    }

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

    @PatchMapping("/{bandId}/musicians")
    public ResponseEntity<BandSummaryDTO> addMusician(@PathVariable Long bandId, @RequestParam Long musicianId) {
        Band band = bandService.addMusician(bandId, musicianId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @DeleteMapping("/{bandId}/musicians")
    public ResponseEntity<BandSummaryDTO> removeMusician(@PathVariable Long bandId, @RequestParam Long musicianId) {
        Band band = bandService.removeMusician(bandId, musicianId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @PatchMapping("/{bandId}/musicstyles")
    public ResponseEntity<BandSummaryDTO> addMusicStyle(@PathVariable Long bandId, @RequestParam Long styleId) {
        Band band = bandService.addMusicStyle(bandId, styleId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @DeleteMapping("/{bandId}/musicstyles")
    public ResponseEntity<BandSummaryDTO> removeMusicStyle(@PathVariable Long bandId, @RequestParam Long styleId) {
        Band band = bandService.removeMusicStyle(bandId, styleId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @PatchMapping("/{bandId}/instruments")
    public ResponseEntity<BandSummaryDTO> addInstrument(@PathVariable Long bandId, @RequestParam Long instrumentId) {
        Band band = bandService.addInstrument(bandId, instrumentId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

    @DeleteMapping("/{bandId}/instruments")
    public ResponseEntity<BandSummaryDTO> removeInstrument(@PathVariable Long bandId, @RequestParam Long instrumentId) {
        Band band = bandService.removeInstrument(bandId, instrumentId);
        return ResponseEntity.ok(bandMapper.toSummaryDTO(band));
    }

}


