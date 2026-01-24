package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.entities.Musician;
import com.jspss.bandbooking.exceptions.NotFoundException;
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
    void createMusicians() {
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
    void createMusician() {
    }

    @Test
    void updateMusician() {
    }

    @Test
    void addInstruments() {
    }

    @Test
    void removeInstruments() {
    }

    @Test
    void addMusicStyles() {
    }

    @Test
    void removeMusicStyle() {
    }

    @Test
    void deleteMusician() {
    }
}