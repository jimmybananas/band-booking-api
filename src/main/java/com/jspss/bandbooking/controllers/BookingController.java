package com.jspss.bandbooking.controllers;

import com.jspss.bandbooking.dto.responses.BookingResponseDTO;
import com.jspss.bandbooking.dto.requests.CheckAvailabilityRequest;
import com.jspss.bandbooking.dto.requests.create.CreateBookingRequest;
import com.jspss.bandbooking.dto.requests.updates.UpdateBookingStatus;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.Booking;
import com.jspss.bandbooking.entities.enums.BookingStatus;
import com.jspss.bandbooking.mappers.BookingMapper;
import com.jspss.bandbooking.repositories.MusicStyleRepository;
import com.jspss.bandbooking.repositories.MusicianRepository;
import com.jspss.bandbooking.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final MusicianRepository musicianRepository;
    private final MusicStyleRepository musicStyleRepository;

    @Operation(
            summary = "Create a new booking",
            description = "Creates a booking for a client and band with the specified time and location."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking request"),
            @ApiResponse(responseCode = "404", description = "Client or band not found")
    })
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

    @Operation(
            summary = "Assign musicians to a booking",
            description = "Automatically assigns musicians based on required instruments and availability."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Musicians assigned"),
            @ApiResponse(responseCode = "400", description = "Musicians already assigned or missing requirements"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{bookingId}/assign")
    public ResponseEntity<BookingResponseDTO> assignMusicians(@PathVariable Long bookingId){
        Booking booking = bookingService.assignMusicians(bookingId);
        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

    @Operation(
            summary = "Update booking status",
            description = "Updates the status of a booking (Pending, Confirmed, Completed, Cancelled)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<BookingSummaryDTO> updateStatus(@PathVariable Long bookingId, @Valid @RequestBody UpdateBookingStatus status){
        Booking booking = bookingService.updateStatus(bookingId, status.status());
        return ResponseEntity.ok(bookingMapper.toBookingSummary(booking));
    }

    @Operation(summary = "Get booking by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking retrieved"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
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

    @Operation(summary = "Get all bookings for a band")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings found"),
            @ApiResponse(responseCode = "404", description = "Band not found")
    })
    @GetMapping("/band/{bandId}")
    public ResponseEntity<List<BookingSummaryDTO>> getBookingsForBand(@PathVariable Long bandId){
        return ResponseEntity.ok(bookingService.getBookingsForBand(bandId));
    }

    @Operation(summary = "Get all bookings for a musician")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings found"),
            @ApiResponse(responseCode = "404", description = "Musicians not found.")
    })
    @GetMapping("/musician/{musicianId}")
    public ResponseEntity<List<BookingSummaryDTO>> getBookingsForMusician(@PathVariable Long musicianId){
        return ResponseEntity.ok(bookingService.getBookingsForMusician(musicianId));
    }

    @Operation(summary = "Get all bookings for a client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<BookingSummaryDTO>> getBookingsForClient(@PathVariable Long clientId){
        return ResponseEntity.ok(bookingService.getBookingsForClient(clientId));
    }

    @Operation(summary = "Cancel a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled."),
            @ApiResponse(responseCode = "400", description = "Booking cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking cannot be found.")
    })
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingSummaryDTO> cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(bookingMapper.toBookingSummary(booking));
    }

    @PostMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(@Valid @RequestBody CheckAvailabilityRequest request) {
        boolean available = bookingService.isBandAvailable(
                request.bandId(),
                request.start(),
                request.end());
        return ResponseEntity.ok(available);
    }

}
