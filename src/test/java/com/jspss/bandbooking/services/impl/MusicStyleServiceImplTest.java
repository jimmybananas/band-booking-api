package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
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
class MusicStyleServiceImplTest {
    @Mock
    private MusicStyleRepository musicStyleRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private MusicStyleServiceImpl musicStyleService;

    @Test
    void getMusicStyle_whenValid_returnsMusicStyle() {
        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setId(1L);

        when(musicStyleRepository.findById(1L)).thenReturn(Optional.of(musicStyle));

        MusicStyle result = musicStyleService.getMusicStyle(1L);

        assertSame(musicStyle, result);
    }

    @Test
    void getMusicStyle_whenNotFound_throwsNotFound(){
        when(musicStyleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> musicStyleService.getMusicStyle(1L));
    }

    @Test
    void getAllMusicStyles() {
        List<MusicStyle> styleList = new ArrayList<>(List.of(new MusicStyle(), new MusicStyle()));

        when(musicStyleRepository.findAll()).thenReturn(styleList);

        List<MusicStyle> results = musicStyleService.getAllMusicStyles();

        assertSame(styleList, results);
        assertEquals(2, results.size());

        verify(musicStyleRepository).findAll();
    }

    @Test
    void createMusicStyle_whenValid_createAndLog() {
        String styleName = "Mambo";

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setId(1L);
        musicStyle.setName(styleName);

        when(musicStyleRepository.findById(1L)).thenReturn(Optional.of(musicStyle));

        MusicStyle result = musicStyleService.createMusicStyle(styleName);

        assertSame(musicStyle, result);
        assertEquals(1L, result.getId());
        assertEquals(styleName, result.getName());

        verify(musicStyleRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void createMusicStyles() {
        List<String> musicStlyeNameList = new ArrayList<>(List.of("Swing", "Trap"));

        MusicStyle swingStyle = new MusicStyle();
        swingStyle.setId(1L);
        swingStyle.setName(musicStlyeNameList.get(0));

        MusicStyle trapStyle = new MusicStyle();
        trapStyle.setId(2L);
        trapStyle.setName(musicStlyeNameList.get(1));

        List<MusicStyle> styleList = new ArrayList<>(List.of(swingStyle, trapStyle));

        when(musicStyleRepository.findById(1L)).thenReturn(Optional.of(swingStyle));
        when(musicStyleRepository.findById(2L)).thenReturn(Optional.of(trapStyle));

        List<MusicStyle> results = musicStyleService.createMusicStyles(musicStlyeNameList);

        assertSame(styleList, results);
        assertEquals("Swing", results.get(0).getName());
        assertEquals("Trap", results.get(1).getName());
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());

        verify(musicStyleRepository, timeout(2)).save(any(MusicStyle.class));
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void searchMusicStyles() {
    }

    @Test
    void updateMusicStyle() {
    }

    @Test
    void deleteMusicStyle() {
    }
}