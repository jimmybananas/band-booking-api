package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.updates.UpdateBookingStatus;
import com.jspss.bandbooking.dto.summaries.BookingSummaryDTO;
import com.jspss.bandbooking.entities.*;
import com.jspss.bandbooking.entities.enums.BookingStatus;
import com.jspss.bandbooking.exceptions.BadRequestException;
import com.jspss.bandbooking.exceptions.NotFoundException;
import com.jspss.bandbooking.mappers.BookingMapper;
import com.jspss.bandbooking.repositories.BandRepository;
import com.jspss.bandbooking.repositories.BookingRepository;
import com.jspss.bandbooking.repositories.ClientRepository;
import com.jspss.bandbooking.repositories.MusicianRepository;
import com.jspss.bandbooking.services.AuditLogger;
import com.jspss.bandbooking.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BandRepository bandRepository;
    private final ClientRepository clientRepository;
    private final MusicianRepository musicianRepository;
    private final BookingMapper bookingMapper;
    private final AuditLogger auditLogger;

    @Override
    @Transactional
    public Booking createBooking(Long clientId,
                                 Long bandId,
                                 ZonedDateTime start,
                                 ZonedDateTime end,
                                 String city,
                                 String state) {
        if(start == null || end == null || end.isBefore(start) ) {
            throw new BadRequestException("Invalid booking time range.");
        }

        Client client = clientRepository.findById(clientId).
                orElseThrow(()-> new NotFoundException("Client not found"));
        Band band = bandRepository.findById(bandId).
                orElseThrow(()-> new NotFoundException("Band not found"));

        if(!isBandAvailable(bandId, start, end)) {
            throw new BadRequestException("Band is not available.");
        }

        Booking booking = new Booking();
        booking.setClient(client);
        booking.setBand(band);
        booking.setGigStarts(start);
        booking.setGigEnds(end);
        booking.setCity(city);
        booking.setState(state);
        booking.setBookingStatus(BookingStatus.Pending);
        bookingRepository.save(booking);

        auditLogger.log(
                "CREATE_BOOKING",
                "Booking",
                booking.getId(),
                "Create booking for Client " + booking.getClient().getId()
        );

        return booking;
    }

    @Override
    @Transactional
    public Booking assignMusicians(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new NotFoundException("Booking not found"));

        if(booking.getBookingStatus().equals(BookingStatus.Confirmed)){
            throw new BadRequestException("Musicians already assigned for this booking.");
        }

        Band band = booking.getBand();
        List<Instrument> requiredInstruments = band.getRequiredInstruments();

        if(band.getRequiredInstruments().isEmpty()){
            throw new BadRequestException("Required instruments have not been assigned.");
        }

        List<MusicStyle> bandMusicStyles = new ArrayList<>(band.getMusicStyles());

        List<Musician> available = musicianRepository.findAvailableMusicians(
                band.getId(),
                booking.getGigStarts(),
                booking.getGigEnds()
        );

        List<Musician> assigned = new ArrayList<>();

        for(Instrument instrument: requiredInstruments) {
            List<Musician> candidates = available.stream()
                    .filter(m-> m.getInstruments().contains(instrument))
                    .filter(m-> m.getMusicStyles().stream().anyMatch(bandMusicStyles::contains))
                    .toList();
            if(candidates.isEmpty()) {
                throw new BadRequestException("No musician for required instrument: " + instrument.getName());
            }

            candidates = candidates.stream()
                    .sorted(Comparator.comparingInt(
                                    (Musician m) -> (int) m.getMusicStyles().stream()
                                    .filter(bandMusicStyles::contains)
                                    .count()
                    ).reversed())
                    .toList();

            Musician chosen = candidates.get(0);
            assigned.add(chosen);
            available.remove(chosen);

        }
        booking.setMusicianList(assigned);
        booking.setBookingStatus(BookingStatus.Confirmed);
        bookingRepository.save(booking);

        auditLogger.log(
                "ASSIGN_MUSICIANS",
                "Booking",
                bookingId,
                "Assigned musicians for Booking ID: " + bookingId
        );

        return booking;
    }

    @Override
    public boolean isBandAvailable(Long bandId, ZonedDateTime start, ZonedDateTime end) {
        return bookingRepository.findConflictBookings(bandId, start, end).isEmpty();
    }

    @Override
    @Transactional
    public Booking updateStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(()-> new NotFoundException("Booking not found"));

        String before = booking.getBookingStatus().toString();

        booking.setBookingStatus(status);

        String after = booking.getBookingStatus().toString();

        bookingRepository.save(booking);

        auditLogger.log(
                "STATUS_UPDATE",
                "Booking",
                booking.getId(),
                "Booking status changed from " + before + "to " + after + "."
        );
        return booking;
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new BadRequestException("Booking does not exist."));
        BookingStatus status = booking.getBookingStatus();
        if(status.equals(BookingStatus.Completed)) {
            throw new BadRequestException("Booking cannot be cancelled as it has already been completed.");
        }
        else if(booking.getBookingStatus().equals(BookingStatus.Cancelled)){
            throw new BadRequestException("Booking already cancelled");
        }
        else {
            booking.setBookingStatus(BookingStatus.Cancelled);
        }
        bookingRepository.save(booking);

        auditLogger.log(
                "CANCEL_BOOKING",
                "Booking",
                bookingId,
                "Booking (ID:" + bookingId + ") cancelled."
        );

        return booking;
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(()-> new NotFoundException("Booking not found"));
    }

    @Override
    public List<Booking> getAllBookings(){
        return bookingRepository.findAll();
    }


    @Override
    public List<BookingSummaryDTO> getBookingsForBand(Long bandId) {
        return bookingRepository.findByBand_Id(bandId)
                .stream()
                .map(bookingMapper::toBookingSummary)
                .toList();
    }

    @Override
    public List<BookingSummaryDTO> getBookingsForMusician(Long musicianId) {
        return bookingRepository.findByMusicianList_Id(musicianId)
                .stream()
                .map(bookingMapper::toBookingSummary)
                .toList();
    }

    @Override
    public List<BookingSummaryDTO> getBookingsForClient(Long clientId) {
        return bookingRepository.findByClient_Id(clientId)
                .stream()
                .map(bookingMapper::toBookingSummary)
                .toList();
    }
}
