package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateBandRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BandServiceImplTest {
    @Mock
    private BandRepository bandRepository;

    @Mock
    private MusicianRepository musicianRepository;

    @Mock
    private MusicStyleRepository musicStyleRepository;

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private BandServiceImpl bandService;

    @Test
    void getBand_returnsBand_whenFound() {
        Band band = new Band();
        band.setId(1L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));

        Band result = bandService.getBand(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getBand_throws_whenNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.getBand(1L));
    }

    @Test
    void getAllBands() {
        List<Band> bands = List.of(new Band(), new Band());
        when(bandRepository.findAll()).thenReturn(bands);

        List<Band> result = bandService.getAllBands();

        assertEquals(2, result.size());
        assertSame(bands, result);
    }

    @Test
    void createBand_whenValid() {
        Band band = new Band();
        band.setBandName("Test Band");

        when(bandRepository.save(any())).thenReturn(band);

        Band result = bandService.createBand("Test Band");

        assertEquals("Test Band", result.getBandName());
        verify(bandRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void createBand_whenNameExists_throwsBadRequest() {
        String inputName = "The Rockers";
        String normalized = "the rockers";

        when(bandRepository.existsByNameIgnoreCase(normalized)).thenReturn(true);

        assertThrows(BadRequestException.class, ()-> bandService.createBand(inputName));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void updateBand_whenValid_updatesBand() {
        Band existing = new Band();
        existing.setId(1L);
        existing.setBandName("Old Name");

        UpdateBandRequest request = new UpdateBandRequest(
                "New Name",
                List.of(),
                List.of(),
                List.of()
        );

        when(bandRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bandRepository.existsByNameIgnoreCase("new name")).thenReturn(false);
        when(bandRepository.save(any())).thenReturn(existing);

        Band result = bandService.updateBand(1L, request);

        assertEquals("New Name", result.getBandName());
        verify(bandRepository).save(existing);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateBand_whenBandNotFound_throwsNotFound(){
        UpdateBandRequest request = new UpdateBandRequest(
                "new band",
                List.of(),
                List.of(),
                List.of()
        );

        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.updateBand(1L, request));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void updateBand_whenNameExists_throwsBadRequest() {
        Band existing = new Band();
        existing.setId(1L);
        existing.setBandName("Old Band Name");

        when(bandRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bandRepository.existsByNameIgnoreCase("new name")).thenReturn(false);
        when(bandRepository.save(any())).thenReturn(existing);

        UpdateBandRequest request = new UpdateBandRequest(
                "new name",
                List.of(),
                List.of(),
                List.of()
        );

        when(bandRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bandRepository.existsByNameIgnoreCase("new name")).thenReturn(true);

        assertThrows(BadRequestException.class, ()-> bandService.updateBand(1L, request));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());

    }

    @Test
    void searchBand_returnsAll_whenQueryEntered() {
        List<Band> bands = List.of(new Band(), new Band());
        when(bandRepository.findAll()).thenReturn(bands);

        List<Band> result = bandService.searchBands("");

        assertEquals(2, result.size());
        assertSame(bands, result);
    }

    @Test
    void searchBand_returnsAll_whenQueryProvided() {
        List<Band> bands = List.of(new Band());
        when(bandRepository.findByBandNameContainingIgnoreCase("Rock")).thenReturn(bands);

        List<Band> result = bandService.searchBands("Rock");

        assertEquals(1, result.size());
        assertSame(bands, result);
    }

    @Test
    void addMusician_whenValid_addMusician() {
        Band band = new Band();
        band.setId(1L);
        band.setBandMembers(new ArrayList<>());

        Musician musician = new Musician();
        musician.setId(10L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicianRepository.findById(10L)).thenReturn(Optional.of(musician));

        Band result = bandService.addMusician(1L, 10L);

        assertTrue(result.getBandMembers().contains(musician));
        verify(bandRepository).save(band);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void addMusician_whenBandNotFound_throwsNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.addMusician(1L, 10L));

        verify(musicianRepository, never()).findById(any());
        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addMusician_whenMusicianNotFound_throwsNotFound(){
        Band band = new Band();
        band.setId(1L);
        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicianRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.addMusician(1L,10L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addMusician_whenAlreadyInBand_throwsBadRequest(){
        Band band = new Band();
        band.setId(1L);
        Musician musician = new Musician();
        musician.setId(10L);

        band.setBandMembers(new ArrayList<>(List.of(musician)));

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicianRepository.findById(10L)).thenReturn(Optional.of(musician));

        assertThrows(BadRequestException.class, ()-> bandService.addMusician(1L, 10L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeMusician_whenValid_RemoveMusician() {
        Band band = new Band();
        band.setId(1L);

        Musician musician = new Musician();
        musician.setId(10L);

        band.setBandMembers(new ArrayList<>(List.of(musician)));

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicianRepository.findById(10L)).thenReturn(Optional.of(musician));
        when(bandRepository.save(any())).thenReturn(band);

        Band result = bandService.removeMusician(1L, 10L);

        assertFalse(result.getBandMembers().contains(musician));
        verify(bandRepository).save(band);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void removeMusician_whenBandNotFound_throwsNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.removeMusician(1L, 10L));

        verify(musicianRepository, never()).findById(any());
        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeMusician_whenMusicianNotFound_throwsNotFound(){
        Band band = new Band();
        band.setId(1L);
        band.setBandMembers(new ArrayList<>());

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicianRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.removeMusician(1L, 10L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeMusician_whenNotInBand_throwsBadRequest(){
        Band band = new Band();
        band.setId(1L);
        band.setBandMembers(new ArrayList<>());

        Musician musician = new Musician();
        musician.setId(10L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicianRepository.findById(10L)).thenReturn(Optional.of(musician));

        assertThrows(BadRequestException.class, ()-> bandService.removeMusician(1L, 10L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addMusicStyle_whenValid_addMusicStyle() {
        Band band = new Band();
        band.setId(1L);
        band.setMusicStyles(new ArrayList<>());

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setId(20L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicStyleRepository.findById(20L)).thenReturn(Optional.of(musicStyle));
        when(bandRepository.save(any())).thenReturn(band);

        Band result = bandService.addMusicStyle(1L, 20L);

        assertTrue(result.getMusicStyles().contains(musicStyle));

        verify(bandRepository).save(band);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void addMusicStyle_whenBandNotFound_throwsNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.addMusicStyle(1L, 20L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addMusicStyle_whenMusicStyleNotFound_throwsNotFound(){
        Band band = new Band();
        band.setId(1L);
        band.setMusicStyles(new ArrayList<>());

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicStyleRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.addMusicStyle(1L, 20L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addMusicStyle_whenMusicStyleAlreadyExistInBand_throwsBadRequest(){
        Band band = new Band();
        band.setId(1L);

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setId(20L);

        band.setMusicStyles(new ArrayList<>(List.of(musicStyle)));

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicStyleRepository.findById(20L)).thenReturn(Optional.of(musicStyle));

        assertThrows(BadRequestException.class, ()-> bandService.addMusicStyle(1L, 20L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeMusicStyle_whenValid_RemoveMusicStyle() {
        Band band = new Band();
        band.setId(1L);

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setId(20L);

        band.setMusicStyles(new ArrayList<>(List.of(musicStyle)));

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicStyleRepository.findById(20L)).thenReturn(Optional.of(musicStyle));
        when(bandRepository.save(any())).thenReturn(band);

        Band result = bandService.removeMusicStyle(1L, 20L);

        assertFalse(result.getMusicStyles().contains(musicStyle));

        verify(bandRepository).save(result);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void removeMusicStyle_whenBandNotFound_throwsNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.removeMusicStyle(1L, 20L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeMusicStyle_whenStyleNotFound_throwsNotFound(){
        Band band = new Band();
        band.setId(1L);
        band.setMusicStyles(new ArrayList<>());

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicStyleRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.removeMusicStyle(1L, 20L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeMusicStyle_whenStyleNotInBand_throwsBadRequest(){
        Band band = new Band();
        band.setId(1L);

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setId(20L);

        band.setMusicStyles(new ArrayList<>());

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(musicStyleRepository.findById(20L)).thenReturn(Optional.of(musicStyle));

        assertThrows(BadRequestException.class, ()-> bandService.removeMusicStyle(1L, 20L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addInstrument_whenValid_AddInstrument() {
        Band band = new Band();
        band.setId(1L);
        band.setRequiredInstruments(new ArrayList<>());

        Instrument instrument = new Instrument();
        instrument.setId(30L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(instrumentRepository.findById(30L)).thenReturn(Optional.of(instrument));
        when(bandRepository.save(any())).thenReturn(band);

        Band result = bandService.addInstrument(1L, 30L);

        assertTrue(result.getRequiredInstruments().contains(instrument));

        verify(bandRepository).save(band);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void addInstrument_whenBandNotFound_throwsNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.addInstrument(1L, 30L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addInstrument_whenInstrumentNotFound_throwNotFound(){
        Band band = new Band();
        band.setId(1L);
        band.setRequiredInstruments(new ArrayList<>());

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(instrumentRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.addInstrument(1L, 30L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void addInstrument_whenInstrumentExistInBand_throwsBadRequest(){
        Band band = new Band();
        band.setId(1L);

        Instrument instrument = new Instrument();
        instrument.setId(30L);

        band.setRequiredInstruments(new ArrayList<>(List.of(instrument)));

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(instrumentRepository.findById(30L)).thenReturn(Optional.of(instrument));

        assertThrows(BadRequestException.class, ()-> bandService.addInstrument(1L, 30L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeInstrument_whenValid_removesInstrument() {
        Band band = new Band();
        band.setId(1L);

        Instrument instrument = new Instrument();
        instrument.setId(30L);

        band.setRequiredInstruments(new ArrayList<>(List.of(instrument)));

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(instrumentRepository.findById(30L)).thenReturn(Optional.of(instrument));
        when(bandRepository.save(any())).thenReturn(band);

        Band result = bandService.removeInstrument(1L, 30L);

        assertFalse(result.getRequiredInstruments().contains(instrument));

        verify(bandRepository).save(band);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void removeInstrument_whenBandNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.removeInstrument(1L, 30L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeInstrument_whenInstrumentNotFound_throwsNotFound(){
        Band band = new Band();
        band.setId(1L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(instrumentRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.removeInstrument(1L, 30L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void removeInstrument_whenInstrumentNotInBand_throwsBadRequest(){
        Band band = new Band();
        band.setId(1L);
        band.setRequiredInstruments(new ArrayList<>());

        Instrument instrument = new Instrument();
        instrument.setId(30L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(instrumentRepository.findById(30L)).thenReturn(Optional.of(instrument));

        assertThrows(BadRequestException.class, ()-> bandService.removeInstrument(1L, 30L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void deleteBand_whenValid_deleteBand() {
        Band band = new Band();
        band.setId(1L);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));

        bandService.deleteBand(1L);

        assertFalse(bandRepository.findAll().contains(band));

        verify(bandRepository).delete(band);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void deleteBand_whenBandNotFound_throwsNotFound(){
        when(bandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bandService.deleteBand(1L));

        verify(bandRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }
}