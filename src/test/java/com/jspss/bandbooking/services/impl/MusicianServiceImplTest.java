package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateMusicianRequest;
import com.jspss.bandbooking.entities.*;
import com.jspss.bandbooking.exceptions.BadRequestException;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.InstrumentRepository;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
import com.jspss.bandbooking.repositories.MusicianRepository;
import com.jspss.bandbooking.services.AuditLogger;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicianServiceImplTest {

    @Mock
    private MusicianRepository musicianRepository;

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private MusicStyleRepository musicStyleRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private MusicianServiceImpl musicianService;

    @Test
    void getMusician_whenValid_returnsMusician() {
        Musician musician = new Musician();
        musician.setId(1L);

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));

        Musician result = musicianService.getMusician(1L);

        assertSame(musician, result);
    }

    @Test
    void getMusician_whenNotFound_throwsNotFound(){
        when(musicianRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> musicianService.getMusician(1L));
    }

    @Test
    void getAllMusicians() {
        List<Musician> musicianList = new ArrayList<>(List.of(new Musician(), new Musician()));

        when(musicianRepository.findAll()).thenReturn(musicianList);

        List<Musician> results = musicianService.getAllMusicians();

        assertEquals(musicianList, results);

        verify(musicianRepository).findAll();
    }

    @Test
    void searchMusicians_whenQueryEmpty_returnsAllMusicians() {
        List<Musician> musicianList = new ArrayList<>(List.of(new Musician(), new Musician()));

        when(musicianRepository.findAll()).thenReturn(musicianList);

        List<Musician> results = musicianService.searchMusicians("");

        assertSame(musicianList, results);
        assertEquals(2, results.size());

        verify(musicianRepository).findAll();
        verify(musicianRepository, never()).fuzzySearch("");
    }

    @Test
    void searchMusicians_whenQueryProvided_callFuzzySearch(){
        List<Musician> foundMusicians = new ArrayList<>(List.of(new Musician(), new Musician()));

        when(musicianRepository.fuzzySearch("John")).thenReturn(foundMusicians);

        List<Musician> results = musicianService.searchMusicians("John");

        assertSame(foundMusicians, results);
        assertEquals(2, results.size());

        verify(musicianRepository).fuzzySearch("John");
        verify(musicianRepository, never()).findAll();
    }

    @Test
    void createMusicians_createAndLog() {
        List<String> musicianNameList = new ArrayList<>(List.of("Dave Mustang", "Mark Hoppus"));

        Musician musician1 = new Musician();
        musician1.setId(1L);
        musician1.setFullName(musicianNameList.get(0));

        Musician musician2 = new Musician();
        musician2.setId(2L);
        musician2.setFullName(musicianNameList.get(1));

        List<Musician> musicianList = new ArrayList<>(List.of(musician1, musician2));

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician1));
        when(musicianRepository.findById(2L)).thenReturn(Optional.of(musician2));

        List<Musician> results = musicianService.createMusicians(musicianNameList);

        assertSame(musicianList, results);
        assertEquals(musicianList.get(0).getFullName(), results.get(0).getFullName());
        assertEquals(musicianList.get(1).getFullName(), results.get(1).getFullName());
        assertEquals(musicianList.get(0).getId(), results.get(0).getId());
        assertEquals(musicianList.get(1).getId(), results.get(1).getId());

        verify(musicianRepository, times(2)).save(any(Musician.class));
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void createMusician_whenValid_createAndLog() {
        String name = "John Johnson";

        Musician musician = new Musician();
        musician.setId(1L);
        musician.setFullName(name);

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));

        Musician result = musicianService.createMusician(name);

        assertSame(musician, result);
        assertEquals(name, result.getFullName());
        assertEquals(1L, result.getId());

        verify(musicianRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateMusician_whenValid_updateAndLog() {
        Musician musician = new Musician();
        musician.setId(1L);
        musician.setEmail("email@gmail.com");
        musician.setFullName("Bob the Builder");
        musician.setPhoneNumber("555-555-5555");


        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));
        when(musicianRepository.save(any(Musician.class))).thenReturn(musician);

        UpdateMusicianRequest request = new UpdateMusicianRequest(
                "Bob the Builder",
                "email@email.com",
                "555-555-5555",
                List.of(1L, 2L, 3L),
                List.of(1L,2L),
                List.of(1L),
                List.of(2L,5L)
        );

        Musician result = musicianService.updateMusician(1L, request);

        assertEquals("Bob the Builder", result.getFullName());
        assertEquals("email@email.com", result.getEmail());
        assertEquals("555-555-5555", result.getPhoneNumber());

        assertEquals(List.of(1L, 2L, 3L), result.getInstruments().stream().map(Instrument::getId).toList());
        assertEquals(List.of(1L, 2L), result.getMusicStyles().stream().map(MusicStyle::getId).toList());
        assertEquals(List.of(1L), result.getBands().stream().map(Band::getId).toList());
        assertEquals(List.of(2L, 5L), result.getBookingsList().stream().map(Booking::getId).toList());

        verify(musicianRepository).findById(1L);
        verify(musicianRepository).save(any(Musician.class));
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateMusician_whenNotFound_throwsNotFound(){
        when(musicianRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> musicianService.updateMusician(1L, any()));

        verify(musicianRepository, never()).save(any());
    }

    @Test
    void addInstruments() {
        Musician musician = new Musician();
        musician.setId(1L);
        musician.setInstruments(new ArrayList<>());

        Instrument instrument1 = new Instrument();
        instrument1.setId(10L);

        Instrument instrument2 = new Instrument();
        instrument2.setId(20L);

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));
        when(instrumentRepository.findById(10L)).thenReturn(Optional.of(instrument1));
        when(instrumentRepository.findById(20L)).thenReturn(Optional.of(instrument2));

        Musician result = musicianService.addInstruments(1L, List.of(10L, 20L));

        assertSame(musician, result);
        assertTrue(result.getInstruments().contains(instrument1));
        assertTrue(result.getInstruments().contains(instrument2));
        assertEquals(2, result.getInstruments().size());

        verify(musicianRepository).save(musician);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void addInstrument_musicianNotFound_throwsNotFound(){
        Instrument instrument = new Instrument();
        instrument.setId(10L);

        when(instrumentRepository.findById(10L)).thenReturn(Optional.of(instrument));
        when(musicianRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> musicianService.addInstruments(1L, List.of(10L)));

        verify(musicianRepository, never()).save(any());
    }

    @Test
    void addInstrument_instrumentNotFound_throwsNotFound(){
        Musician musician = new Musician();
        musician.setId(1L);

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));
        when(instrumentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> musicianService.addInstruments(1L, List.of(10L)));

        verify(musicianRepository, never()).save(any());
    }

    @Test
    void removeInstruments_whenValid_removeAndLog() {
        Musician musician = new Musician();
        musician.setId(1L);
        musician.setInstruments(new ArrayList<>());

        Instrument instrument = new Instrument();
        instrument.setId(10L);

        musician.setInstruments(List.of(instrument));

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));
        when(instrumentRepository.findById(10L)).thenReturn(Optional.of(instrument));
        when(musicianRepository.save(any())).thenReturn(musician);

        Musician result = musicianService.removeInstruments(1L, List.of(10L));

        assertFalse(result.getInstruments().contains(instrument));
        verify(musicianRepository).save(musician);
        verify(auditLogger).log(any(), any(), any(), any());

    }

    @Test
    void removeInstrument_whenInstrumentNotInList_badRequest(){
        Musician musician = new Musician();
        musician.setId(1L);
        musician.setInstruments(new ArrayList<>());

        Instrument instrument = new Instrument();
        instrument.setId(10L);

        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));
        when(instrumentRepository.findById(10L)).thenReturn(Optional.of(instrument));

        assertThrows(BadRequestException.class, ()-> musicianService.removeInstruments(1L, List.of(10L)));

        verify(musicianRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());

    }


    @Test
    void addMusicStyles_whenValid_addAndLog() {
        Musician musician = new Musician();
        musician.setId(1L);
        musician.setMusicStyles(new ArrayList<>());

        MusicStyle style1 = new MusicStyle();
        style1.setId(10L);
        MusicStyle style2 = new MusicStyle();
        style2.setId(20L);


        when(musicianRepository.findById(1L)).thenReturn(Optional.of(musician));
        when(musicStyleRepository.findById(10L)).thenReturn(Optional.of(style1));
        when(musicStyleRepository.findById(10L)).thenReturn(Optional.of(style2));

        Musician result = musicianService.addMusicStyles(1L, List.of(10L, 20L));

        assertEquals(List.of(style1, style2), result.getMusicStyles());
        assertEquals(2, result.getMusicStyles().size());
        assertTrue(result.getMusicStyles().contains(style1));
        assertTrue(result.getMusicStyles().contains(style2));

        verify(musicianRepository).save(musician);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void removeMusicStyle() {
    }

    @Test
    void deleteMusician() {
    }
}