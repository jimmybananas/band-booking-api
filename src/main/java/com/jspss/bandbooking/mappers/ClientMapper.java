package com.jspss.bandbooking.mappers;

import com.jspss.bandbooking.dto.responses.ClientResponseDTO;
import com.jspss.bandbooking.dto.requests.create.CreateClientRequest;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    private final BookingMapper bookingMapper;

    public ClientMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    public ClientResponseDTO toDTO(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getBookingList().stream().map(Booking::getId).toList()
        );
    }

    public Client fromCreateRequest(CreateClientRequest request){
        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhoneNumber(request.phoneNumber());

        return client;
    }
}
