package com.jspss.bandbooking.services;

import com.jspss.bandbooking.dto.requests.updates.UpdateClientRequest;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.Client;

import java.util.List;

public interface ClientService {
    Client getClient(Long id);
    List<Client> getAllClients();
    List<Client> searchClients(String query);
    Client createClient(String name, String email, String phoneNumber);
    Client updateClient(Long id, UpdateClientRequest request);
    List<BookingSummaryDTO> getBookings(Long id);
}
