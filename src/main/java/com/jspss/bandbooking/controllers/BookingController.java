package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.responses.BookingResponseDTO;
import com.jspss.bandbooking.dto.requests.CheckAvailabilityRequest;
import com.jspss.bandbooking.dto.requests.create.CreateBookingRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateBookingStatus;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.enums.BookingStatus;
import com.jspss.bandbooking.mappers.BookingMapper;
import com.jspss.bandbooking.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody CreateBookingRequest request){
        Booking booking = bookingService.createBooking(
                request.clientId(),
                request.bandId(),
                request.start(),
                request.end(),
                request.city(),
                request.state()
        );
        return ResponseEntity.status(201).body(bookingMapper.toDTO(booking));
    }

    @PostMapping("/{bookingId}/assign")
    public ResponseEntity<BookingResponseDTO> assignMusicians(@PathVariable Long bookingId){
        Booking booking = bookingService.assignMusicians(bookingId);
        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<BookingSummaryDTO> updateStatus(@PathVariable Long bookingId, @Valid @RequestBody UpdateBookingStatus status){
        Booking booking = bookingService.updateStatus(bookingId, status.status());
        return ResponseEntity.ok(bookingMapper.toBookingSummary(booking));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBooking(bookingId);
        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

    @GetMapping
    public ResponseEntity<List<BookingSummaryDTO>> getAllBookings(){
        List<Booking> allBookings = bookingService.getAllBookings();
        return ResponseEntity.ok(allBookings.stream().map(
                bookingMapper::toBookingSummary).toList());
    }

    @GetMapping("/band/{bandId}")
    public ResponseEntity<List<BookingSummaryDTO>> getBookingsForBand(@PathVariable Long bandId){
        return ResponseEntity.ok(bookingService.getBookingsForBand(bandId));
    }

    @GetMapping("/musician/{musicianId}")
    public ResponseEntity<List<BookingSummaryDTO>> getBookingsForMusician(@PathVariable Long musicianId){
        return ResponseEntity.ok(bookingService.getBookingsForMusician(musicianId));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<BookingSummaryDTO>> getBookingsForClient(@PathVariable Long clientId){
        return ResponseEntity.ok(bookingService.getBookingsForClient(clientId));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingSummaryDTO> cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(bookingMapper.toBookingSummary(booking));
    }

    @PostMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(@RequestBody CheckAvailabilityRequest request) {
        boolean available = bookingService.isBandAvailable(
                request.bandId(),
                request.start(),
                request.end());
        return ResponseEntity.ok(available);
    }

}
