package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.requests.create.CreateClientRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateClientRequest;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Band;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.Client;
import com.jspss.bandbooking.mappers.BookingMapper;
import com.jspss.bandbooking.mappers.ClientMapper;
import com.jspss.bandbooking.dto.responses.ClientResponseDTO;
import com.jspss.bandbooking.repositories.AuditLogRepository;
import com.jspss.bandbooking.repositories.ClientRepository;
import com.jspss.bandbooking.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final BookingMapper bookingMapper;
    private final AuditLogRepository auditLogRepository;
    private final ClientRepository clientRepository;

    @Operation(summary = "Get client by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client found."),
            @ApiResponse(responseCode = "404", description = "Client not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClient(@PathVariable Long id){
        Client client = clientService.getClient(id);
        return ResponseEntity.ok(clientMapper.toDTO(client));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients(){
        List<Client> clientList = clientService.getAllClients();
        return ResponseEntity.ok(clientList
                .stream()
                .map(clientMapper::toDTO)
                .toList());
    }

    @Operation(summary = "Create a new client.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client successfully created."),
            @ApiResponse(responseCode = "404", description = "Client could not be created.")
    })
    @PostMapping()
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody CreateClientRequest request){
        Client saved = clientService.createClient(
                request.name(),
                request.email(),
                request.phoneNumber()
        );
        return ResponseEntity.created(URI.create("/api/clients/" + saved.getId()))
                .body(clientMapper.toDTO(saved));
    }

    @Operation(summary = "Update a client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client successfully updated."),
            @ApiResponse(responseCode = "400", description = "Client could not be updated."),
            @ApiResponse(responseCode = "404", description = "Client not found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long id, @Valid @RequestBody UpdateClientRequest request){
        Client updated = clientService.updateClient(id, request);
        return ResponseEntity.ok(clientMapper.toDTO(updated));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDTO>> searchClients(@RequestParam String query) {
        List<Client> clientList = clientService.searchClients(query);
        return ResponseEntity.ok(clientList.stream()
                .map(clientMapper::toDTO).toList());
    }

    @Operation(summary = "Get all bookings for a client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All bookings provided."),
            @ApiResponse(responseCode = "404", description = "Client or bookings not found.")
    })
    @GetMapping("/{clientId}/bookings")
    public ResponseEntity<List<BookingSummaryDTO>> getBookings(@PathVariable Long clientId){
        return ResponseEntity.ok(clientService.getBookings(clientId));
    }
}
