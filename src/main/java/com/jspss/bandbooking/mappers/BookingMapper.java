package com.jspss.bandbooking.mappers;

import com.jspss.bandbooking.dto.responses.BookingResponseDTO;
import com.jspss.bandbooking.dto.requests.create.CreateBookingRequest;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.dto.summaries.MusicianSummaryDTO;
import com.jspss.bandbooking.entities.*;
import com.jspss.bandbooking.entities.enums.BookingStatus;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponseDTO toDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getBand().getBandName(),
                booking.getBand().getId(),
                booking.getClient().getName(),
                booking.getClient().getId(),
                booking.getMusicianList()
                        .stream()
                        .map(this::toMusicianSummary)
                        .toList(),
                booking.getGigStarts(),
                booking.getGigEnds(),
                booking.getCity(),
                booking.getState(),
                booking.getBookingStatus()
        );
    }

    public Booking fromCreateBookingRequest(CreateBookingRequest request) {
        Booking booking = new Booking();
        booking.setGigStarts(request.start());
        booking.setGigEnds(request.end());
        booking.setCity(request.city());
        booking.setState(request.state());
        booking.setBookingStatus(BookingStatus.Pending);

        Client client = new Client();
        client.setId(request.clientId());
        booking.setClient(client);

        Band band = new Band();
        band.setId(request.bandId());
        booking.setBand(band);

        return booking;
    }

    public MusicianSummaryDTO toMusicianSummary(Musician m){
        return new MusicianSummaryDTO(
                m.getId(),
                m.getFullName(),
                m.getInstruments().stream().map(Instrument::getId).toList(),
                m.getMusicStyles().stream().map(MusicStyle::getId).toList()
        );
    }

    public BookingSummaryDTO toBookingSummary (Booking b) {
        return new BookingSummaryDTO(
                b.getId(),
                b.getClient().getId(),
                b.getClient().getName(),
                b.getBand().getId(),
                b.getBand().getBandName(),
                b.getBookingStatus(),
                b.getGigStarts(),
                b.getGigEnds()
        );
    }
}
