package com.jspss.bandbooking.services;

import com.jspss.bandbooking.dto.requests.updates.UpdateBookingStatus;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.enums.BookingStatus;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookingService {

    Booking createBooking(Long clientId,
                          Long bandId,
                          ZonedDateTime start,
                          ZonedDateTime end,
                          String city,
                          String state);

    Booking assignMusicians(Long bookingId);

    boolean isBandAvailable(Long bandId,
                            ZonedDateTime start,
                            ZonedDateTime end);

    Booking updateStatus(Long bookingId, BookingStatus status);
    Booking cancelBooking(Long bookingId);

    Booking getBooking(Long bookingId);
    List<Booking> getAllBookings();

    List<BookingSummaryDTO> getBookingsForBand(Long bandId);

    List<BookingSummaryDTO> getBookingsForMusician(Long musicianId);
    List<BookingSummaryDTO> getBookingsForClient(Long clientId);
}
