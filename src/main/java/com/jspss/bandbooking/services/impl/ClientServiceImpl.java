package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateClientRequest;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.Client;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.mappers.BookingMapper;
import com.jspss.bandbooking.repositories.ClientRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AuditLogger auditLogger;
    private final BookingMapper bookingMapper;

    @Override
    public Client getClient(Long id) {
        return clientRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Client not found")
        );
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> searchClients(String query) {
        if(query == null || query.trim().isEmpty()) {
            return clientRepository.findAll();
        }
        return clientRepository.findByNameContainingIgnoreCase(query);

    }

    @Override
    public Client createClient(String name, String email, String phoneNumber) {

       Client client  = new Client();
       client.setName(name);
       client.setEmail(email);
       client.setPhoneNumber(phoneNumber);

       clientRepository.save(client);

       auditLogger.log(
               "CREATE_CLIENT",
               "Client",
               client.getId(),
               "Client created with ID: " + client.getId()
       );

       return client;
    }

    @Override
    public Client updateClient(Long id, UpdateClientRequest request) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Client not found."));

        String before = existing.toString();

        existing.setName(request.name());
        existing.setEmail(request.email());
        existing.setPhoneNumber(request.phoneNumber());

        clientRepository.save(existing);

        String after = existing.toString();

        auditLogger.log(
                "CLIENT_UPDATE",
                "Client",
                existing.getId(),
                "Updated Client from " + before + " to " + after
        );

        return existing;
    }

    @Override
    public List<BookingSummaryDTO> getBookings(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Client not found"));

        return client.getBookingList()
                .stream()
                .map(bookingMapper::toBookingSummary)
                .toList();
    }
}
