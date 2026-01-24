package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateInstrumentRequest;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.repositories.InstrumentRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.InstrumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class InstrumentServiceImplTest {
    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private InstrumentServiceImpl instrumentService;

    @Test
    void getInstrument_whenValid_returnInstrument() {
        Instrument instrument = new Instrument();
        instrument.setId(1L);

        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        Instrument result = instrumentService.getInstrument(1L);

        assertSame(instrument, result);
    }

    @Test
    void getInstrument_whenNotFound_throwsNotFound(){
        when(instrumentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> instrumentService.getInstrument(1L));
    }

    @Test
    void getAllInstruments() {
        List<Instrument> instrumentList = new ArrayList<>(List.of(new Instrument(), new Instrument()));

        when(instrumentRepository.findAll()).thenReturn(instrumentList);

        List<Instrument> results = instrumentService.getAllInstruments();

        assertSame(instrumentList, results);
        assertEquals(2, results.size());

        verify(instrumentRepository).findAll();
    }

    @Test
    void createInstrument() {
        String name = "Double Bass";

        Instrument instrument = new Instrument();
        instrument.setId(1L);

        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        Instrument result = instrumentService.createInstrument(name);

        assertSame(instrument, result);
        assertEquals(name, result.getName());
        assertEquals(1L, result.getId());

        ArgumentCaptor<Instrument> captor = ArgumentCaptor.forClass(Instrument.class);
        verify(instrumentRepository).save(captor.capture());
        Instrument passedToSave = captor.getValue();

        assertEquals(name, passedToSave.getName());

        verify(auditLogger).log(any(), any(), any(), any());
    }



    @Test
    void createInstruments() {
        String instr1 = "Drums";
        String instr2 = "Bass";
        String instr3 = "Guitar";

        Instrument instrument1 = new Instrument();
        instrument1.setId(10L);

        Instrument instrument2 = new Instrument();
        instrument2.setId(20L);

        Instrument instrument3 = new Instrument();
        instrument3.setId(30L);

        when(instrumentRepository.save(any(Instrument.class)))
                .thenReturn(instrument1)
                .thenReturn(instrument2)
                .thenReturn(instrument3);

        List<String> instrumentNameList = new ArrayList<>(List.of(instr1, instr2, instr3));
        List<Instrument> listResult = instrumentService.createInstruments(instrumentNameList);

        assertEquals(3, listResult.size());

        assertSame(instrument1, listResult.get(0));
        assertSame(instrument2, listResult.get(1));
        assertSame(instrument3, listResult.get(2));

        assertEquals("Drums", listResult.get(0).getName());
        assertEquals("Bass", listResult.get(1).getName());
        assertEquals("Guitar", listResult.get(2).getName());

        verify(instrumentRepository, times(3)).save(any(Instrument.class));
        verify(auditLogger).log(any(), any(), any(), any());

    }

    @Test
    void searchInstruments_whenQueryEmpty_FindAll() {
        Instrument i1 = new Instrument();
        Instrument i2 = new Instrument();
        List<Instrument> instrumentList = new ArrayList<>(List.of(i1, i2));

        when(instrumentRepository.findAll()).thenReturn(instrumentList);

        List<Instrument> results = instrumentService.searchInstruments("");

        assertSame(instrumentList, results);
        assertEquals(2, results.size());

        verify(instrumentRepository).findAll();
        verify(instrumentRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void searchInstrument_whenQueryProvided_callsSearchMethod(){
        Instrument i1 = new Instrument();
        Instrument i2 = new Instrument();
        List<Instrument> foundInstruments = new ArrayList<>(List.of(i1, i2));

        when(instrumentRepository
                .findByNameContainingIgnoreCase("Guitar"))
                .thenReturn(foundInstruments);

        List<Instrument> results = instrumentService.searchInstruments("Guitar");

        assertSame(foundInstruments, results);
        assertEquals(2, results.size());

        verify(instrumentRepository).findByNameContainingIgnoreCase("Guitar");
        verify(instrumentRepository, never()).findAll();

    }

    @Test
    void updateInstrument_whenValid_updateAndLog() {
        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setName("Gootar");

        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        UpdateInstrumentRequest request = new UpdateInstrumentRequest("Guitar");
        Instrument result = instrumentService
                .updateInstrument(1L, request);

        assertEquals(instrument, result);
        assertSame("Guitar", instrument.getName());

        verify(instrumentRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateInstrument_whenNotFound(){
        when(instrumentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> instrumentService.deleteInstrument(1L));

        verify(instrumentRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void deleteInstrument_whenValid(){
        Instrument instrument = new Instrument();
        instrument.setId(1L);

        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        instrumentService.deleteInstrument(1L);

        assertFalse(instrumentRepository.findAll().contains(instrument));

        verify(instrumentRepository).delete(instrument);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void deleteInstrument_whenNotFound_throwsNotFound(){
        when(instrumentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> instrumentService.deleteInstrument(1L));

        verify(instrumentRepository, never()).delete(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }
}