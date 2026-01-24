package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.requests.create.CreateInstrumentRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateInstrumentRequest;
import com.jspss.bandbooking.dto.responses.InstrumentResponseDTO;
import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.mappers.InstrumentMapper;
import com.jspss.bandbooking.services.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;
    private final InstrumentMapper instrumentMapper;


    @GetMapping("/{id}")
    public ResponseEntity<InstrumentResponseDTO> getInstrument(@PathVariable Long id){
        Instrument instrument = instrumentService.getInstrument(id);
        return ResponseEntity.status(200).body(instrumentMapper.toDTO(instrument));
    }

    @PostMapping
    public ResponseEntity<InstrumentResponseDTO> createInstrument(@Valid @RequestBody CreateInstrumentRequest request){
        Instrument instrument =  instrumentService.createInstrument(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(instrumentMapper.toDTO(instrument));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<InstrumentResponseDTO> >createInstruments(@RequestBody List<CreateInstrumentRequest> requests){
        List<String> names = requests.stream().map(CreateInstrumentRequest::name).toList();
        List<Instrument> instrumentList = instrumentService.createInstruments(names);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body((instrumentList.stream().map(instrumentMapper::toDTO).toList()));
    }

    @GetMapping
    public ResponseEntity<List<InstrumentResponseDTO>> getAllInstruments(){
        return ResponseEntity.ok(instrumentService.getAllInstruments()
                .stream()
                .map(instrumentMapper::toDTO)
                .toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstrumentResponseDTO> updateInstrument(@PathVariable Long id,
                                       @Valid @RequestBody UpdateInstrumentRequest request){
        Instrument instrument = instrumentService.updateInstrument(id, request);
        return ResponseEntity.ok(instrumentMapper.toDTO(instrument));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteInstrument(@PathVariable Long id){
        instrumentService.deleteInstrument(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<InstrumentResponseDTO>> searchInstruments(@RequestParam String query) {
        List<Instrument> instruments = instrumentService.searchInstruments(query);
        return ResponseEntity.ok(instruments.stream()
                .map(instrumentMapper::toDTO).toList());
    }
}
