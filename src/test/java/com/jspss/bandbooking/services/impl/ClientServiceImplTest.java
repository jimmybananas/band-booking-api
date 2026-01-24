package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateClientRequest;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.Client;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.mappers.BookingMapper;
import com.jspss.bandbooking.repositories.ClientRepository;
import com.jspss.bandbooking.services.AuditLogger;
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
class ClientServiceImplTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void getClient_whenValid() {
        Client client = new Client();
        client.setId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client result = clientService.getClient(1L);

        assertSame(client, result);
    }

    @Test
    void getClient_whenNoClient_throwsNotFound(){
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> clientService.getClient(1L));
    }

    @Test
    void getAllClients() {
        List<Client> clientList = new ArrayList<>(List.of(new Client(), new Client()));

        when(clientRepository.findAll()).thenReturn(clientList);

        List<Client> results = clientService.getAllClients();

        assertSame(clientList, results);
        assertEquals(2, results.size());

        verify(clientRepository).findAll();
    }

    @Test
    void searchClients_whenQueryIsBlank_returnAllClients() {
        Client c1 = new Client();
        Client c2 = new Client();
        List<Client> clientList = new ArrayList<>(List.of(c1, c2));

        when(clientRepository.findAll()).thenReturn(clientList);

        List<Client> results = clientService.searchClients("");

        assertSame(clientList, results);
        assertEquals(2, results.size());

        verify(clientRepository).findAll();
        verify(clientRepository, never()).searchClients(any());
    }

    @Test
    void searchClients_whenQueryProvided_callsSearchMethod(){
        Client c1 = new Client();
        Client c2 = new Client();
        List<Client> found = new ArrayList<>(List.of(c1, c2));

        when(clientRepository.searchClients("John")).thenReturn(found);

        List<Client> results = clientService.searchClients("John");

        assertSame(found, results);
        assertEquals(2, results.size());

        verify(clientRepository).searchClients("John");
        verify(clientRepository, never()).findAll();
    }

    @Test
    void searchClients_whenNoMatchFound_returnsEmptyList(){
        when(clientRepository.searchClients("xyz")).thenReturn(List.of());

        List<Client> results = clientService.searchClients("xyz");

        assertTrue(results.isEmpty());

        verify(clientRepository).searchClients("xyz");
    }

    @Test
    void createClient_whenValid_createsAndLogs() {
        String name = "First Last";
        String email = "email@email.com";
        String phone = "555-555-5555";

        Client saved = new Client();
        saved.setId(1L);
        saved.setName(name);
        saved.setEmail(email);
        saved.setPhoneNumber(phone);

        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        Client result = clientService.createClient(name, email, phone);

        assertSame(saved, result);
        assertSame(name, result.getName());
        assertSame(email, result.getEmail());
        assertSame(phone, result.getPhoneNumber());

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);

        verify(clientRepository).save(captor.capture());

        Client passedToSave = captor.getValue();

        assertEquals(name, passedToSave.getName());
        assertEquals(email, passedToSave.getEmail());
        assertEquals(phone, passedToSave.getPhoneNumber());

        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateClient_whenValid_updatesAndLogs() {
        Client client = new Client();
        client.setId(1L);
        client.setName("Firstname Lastname");
        client.setEmail("email@email.com");
        client.setPhoneNumber("555-555-5555");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        UpdateClientRequest clientRequest = new UpdateClientRequest(
                "Firstname Lastname",
                "email@GMAIL.com",
                "555-444-5555"
        );

        Client result = clientService.updateClient(1L, clientRequest);

        assertSame(client, result);
        assertEquals("Firstname Lastname", client.getName());
        assertEquals("email@GMAIL.com", client.getEmail());
        assertEquals("555-444-5555", client.getPhoneNumber());


        verify(clientRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateClient_whenClientNotFound_throwsNotFound(){
        UpdateClientRequest request = new UpdateClientRequest(
                "Name",
                "email",
                "phone"
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> clientService.updateClient(1L, request));
    }

    @Test
    void getBookings_whenValid() {
        Client client = new Client();
        client.setId(1L);
        client.setBookingList(List.of(new Booking(), new Booking()));
        List<BookingSummaryDTO> clientListDTO = client.getBookingList()
                .stream().map(bookingMapper::toBookingSummary).toList();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        List<BookingSummaryDTO> results  = clientService.getBookings(1L);

        assertEquals(2, results.size());
        assertEquals(clientListDTO, results);
    }

    @Test
    void getBookings_whenClientNotFound_throwsNotFound(){
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> clientService.getBookings(1L));
    }
}