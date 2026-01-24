package com.jspss.bandbooking.services.impl;

import com.jspss.bandbooking.dto.requests.create.CreateBookingRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private  BookingRepository bookingRepository;

    @Mock
    private  BandRepository bandRepository;

    @Mock
    private  ClientRepository clientRepository;

    @Mock
    private  MusicianRepository musicianRepository;

    @Mock
    private  BookingMapper bookingMapper;

    @Mock
    private  AuditLogger auditLogger;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_whenClientNotFound_throwsBadRequest() {
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        when(clientRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bookingService.createBooking(
                10L,
                20L,
                start,
                end,
                "City",
                "State"
                )
        );

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void createBooking_whenInvalidTimeRange_throwsBadRequest(){
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        assertThrows(BadRequestException.class, ()-> bookingService.createBooking(
                        10L,
                        20L,
                        start,
                        end,
                        "City",
                        "State"
                )
        );

        verify(clientRepository, never()).findById(any());
        verify(bandRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenBandNotAvailable_throwsBadRequest(){
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        when(clientRepository.findById(10L)).thenReturn(Optional.of(new Client()));
        when(bandRepository.findById(20L)).thenReturn(Optional.of(new Band()));
        when(bookingRepository.findConflictBookings(20L, start, end))
                .thenReturn(List.of(new Booking()));

        assertThrows(BadRequestException.class, ()->bookingService.createBooking(
                10L,
                20L,
                start,
                end,
                "City",
                "State"
            )
        );

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void createBooking_whenValid_SaveBookingAndLogs(){
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        Client client = new Client();
        Band band = new Band();

        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        when(bandRepository.findById(20L)).thenReturn(Optional.of(band));

        Booking saved = new Booking();
        saved.setId(30L);

        when(bookingRepository.save(any())).thenReturn(saved);

        Booking result = bookingService.createBooking(
                10L,
                20L,
                start,
                end,
                "City",
                "State"
        );

        assertEquals(30L, result.getId());
        verify(bookingRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void createBooking_whenBandNotFound_throwsNotFound(){
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        when(clientRepository.findById(10L)).thenReturn(Optional.of(new Client()));
        when(bandRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bookingService.createBooking(
                        10L,
                        20L,
                        start,
                        end,
                        "City",
                        "State"
                )
        );

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void assignMusicians_whenBookingNotFound_throwsNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bookingService.assignMusicians(1L));

        verify(musicianRepository, never()).findAvailableMusicians(any(), any(), any());
        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void assignMusicians_whenBookingAlreadyConfirmed_throwsBadRequest(){
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Confirmed);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, ()-> bookingService.assignMusicians(1L));

        verify(musicianRepository, never()).findAvailableMusicians(any(), any(), any());
        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void assignMusicians_whenNoRequiredInstruments_throwsBadRequest(){
        Band band = new Band();
        band.setRequiredInstruments(new ArrayList<>());

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);
        booking.setBand(band);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, ()-> bookingService.assignMusicians(1L));
    }

    @Test
    void assignMusicians_whenNoMusiciansAvailable_throwsBadRequest(){
        Instrument guitar = new Instrument();
        guitar.setName("Guitar");

        MusicStyle musicStyle = new MusicStyle();
        musicStyle.setName("Rock");

        Band band = new Band();
        band.setRequiredInstruments(List.of(guitar));
        band.setMusicStyles(List.of(musicStyle));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);
        booking.setBand(band);
        booking.setGigStarts(ZonedDateTime.now());
        booking.setGigEnds(ZonedDateTime.now().plusHours(2));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(musicianRepository.findAvailableMusicians(any(), any(), any())).thenReturn(List.of()); //empty list

        assertThrows(BadRequestException.class, ()-> bookingService.assignMusicians(1L));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void assignMusician_whenNoInstrumentCoverage_throwsBadRequest(){
        Instrument guitar = new Instrument();
        guitar.setName("Guitar");

        Instrument drums = new Instrument();
        drums.setName("Drums");

        MusicStyle rockStyle = new MusicStyle();
        rockStyle.setName("Rock");

        Band band = new Band();
        band.setRequiredInstruments(List.of(guitar));
        band.setMusicStyles(List.of(rockStyle));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);
        booking.setBand(band);
        booking.setGigStarts(ZonedDateTime.now());
        booking.setGigEnds(ZonedDateTime.now().plusHours(2));

        Musician musician = new Musician();
        musician.setInstruments(List.of(drums));
        musician.setMusicStyles(List.of(rockStyle));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(musicianRepository.findAvailableMusicians(any(), any(), any())).thenReturn(List.of(musician));

        assertThrows(BadRequestException.class, ()-> bookingService.assignMusicians(1L));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());

    }

    @Test
    void assignMusician_whenNoStyleMatch_throwsBadRequest(){
        Instrument guitar = new Instrument();
        guitar.setName("Guitar");

        MusicStyle rockStyle = new MusicStyle();
        rockStyle.setName("Rock");

        Band band = new Band();
        band.setRequiredInstruments(List.of(guitar));
        band.setMusicStyles(List.of(rockStyle));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);
        booking.setBand(band);
        booking.setGigStarts(ZonedDateTime.now());
        booking.setGigEnds(ZonedDateTime.now().plusHours(2));

        MusicStyle jazzStyle = new MusicStyle();
        jazzStyle.setName("Jazz");

        Musician musician = new Musician();
        musician.setInstruments(List.of(guitar));
        musician.setMusicStyles(List.of(jazzStyle));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(musicianRepository.findAvailableMusicians(any(), any(), any())).thenReturn(List.of(musician));

        assertThrows(BadRequestException.class, ()-> bookingService.assignMusicians(1L));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void assignMusicians_pickMusiciansWithMostStyleMatches(){
        Instrument guitar = new Instrument();
        guitar.setName("Guitar");

        MusicStyle rockStyle = new MusicStyle();
        rockStyle.setName("Rock");

        MusicStyle jazzStyle = new MusicStyle();
        rockStyle.setName("Jazz");

        MusicStyle popStyle = new MusicStyle();
        rockStyle.setName("Pop");

        Band band = new Band();
        band.setMusicStyles(List.of(jazzStyle, rockStyle, popStyle));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);
        booking.setBand(band);
        booking.setGigStarts(ZonedDateTime.now());
        booking.setGigEnds(ZonedDateTime.now().plusHours(2));

        Musician m1 = new Musician();
        m1.setMusicStyles(List.of(jazzStyle));
        m1.setInstruments(List.of(guitar));

        Musician m2 = new Musician();
        m2.setMusicStyles(List.of(jazzStyle, rockStyle));
        m2.setInstruments(List.of(guitar));

        Musician m3 = new Musician();
        m3.setMusicStyles(List.of(jazzStyle, rockStyle, popStyle));
        m3.setInstruments(List.of(guitar));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(musicianRepository.findAvailableMusicians(any(), any(), any())).thenReturn(List.of(m1, m2, m3));

        Booking result = bookingService.assignMusicians(1L);

        assertEquals(1, result.getMusicianList().size());
        assertEquals(m3, result.getMusicianList().get(0));

        verify(bookingRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void assignMusicians_whenValid_assignMusiciansAndLog(){
        Instrument guitar = new Instrument();
        guitar.setName("Guitar");

        Instrument drums = new Instrument();
        guitar.setName("Drums");

        MusicStyle rockStyle = new MusicStyle();
        rockStyle.setName("Rock");

        MusicStyle jazzStyle = new MusicStyle();
        rockStyle.setName("Jazz");

        Band band = new Band();
        band.setRequiredInstruments(List.of(guitar, drums));
        band.setMusicStyles(List.of(rockStyle, jazzStyle));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);
        booking.setBand(band);
        booking.setGigStarts(ZonedDateTime.now());
        booking.setGigEnds(ZonedDateTime.now().plusHours(2));

        Musician rockGuitarist = new Musician();
        rockGuitarist.setInstruments(List.of(guitar));
        rockGuitarist.setMusicStyles(List.of(rockStyle));

        Musician jazzDrummer = new Musician();
        jazzDrummer.setInstruments(List.of(drums));
        jazzDrummer.setMusicStyles(List.of(jazzStyle));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(musicianRepository.findAvailableMusicians(any(), any(), any()))
                .thenReturn(List.of(rockGuitarist, jazzDrummer));

        Booking result = bookingService.assignMusicians(1L);

        assertEquals(BookingStatus.Confirmed, result.getBookingStatus());
        assertEquals(2, result.getMusicianList().size());
        assertTrue(result.getMusicianList().contains(rockGuitarist));
        assertTrue(result.getMusicianList().contains(jazzDrummer));

        verify(bookingRepository).save(any());
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void isBandAvailable_returnsTrue_whenNoConflicts() {
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        when(bookingRepository.findConflictBookings(1L, start, end)).thenReturn(List.of());

        boolean result = bookingService.isBandAvailable(1L, start, end);

        assertTrue(result);
    }

    @Test
    void isBandAvailable_returnsFalse_whenConflictExists(){
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        Booking conflict = new Booking();
        conflict.setId(100L);

        when(bookingRepository.findConflictBookings(1L, start, end)).thenReturn(List.of(conflict));

        boolean result = bookingService.isBandAvailable(1L, start, end);

        assertFalse(result);
    }

    @Test
    void updateStatus_whenValid() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Pending);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.updateStatus(1L, BookingStatus.Confirmed);

        assertSame(booking, result);
        assertEquals(BookingStatus.Confirmed, result.getBookingStatus());
        verify(bookingRepository).save(booking);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void updateStatus_whenBookingNotFound_throwsNotFound(){
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bookingService.updateStatus(1L, BookingStatus.Confirmed));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void cancelBooking_whenValid_updateStatusAndLogs(){
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Confirmed);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.cancelBooking(1L);

        assertEquals(BookingStatus.Cancelled, result.getBookingStatus());
        verify(bookingRepository).save(booking);
        verify(auditLogger).log(any(), any(), any(), any());
    }

    @Test
    void cancelBooking_whenBookingNotFound_throwsBadRequest() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, ()-> bookingService.cancelBooking(1L));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void cancelBooking_whenAlreadyCompleted_throwsBadRequest() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Completed);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, ()-> bookingService.cancelBooking(1L));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void cancelBooking_whenAlreadyCancelled_throwsBadRequest(){
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.Cancelled);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, ()-> bookingService.cancelBooking(1L));

        verify(bookingRepository, never()).save(any());
        verify(auditLogger, never()).log(any(), any(), any(), any());
    }

    @Test
    void getBooking_whenValid() {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(1L);

        assertSame(booking, result);
    }

    @Test
    void getBooking_whenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> bookingService.getBooking(1L));
    }

    @Test
    void getAllBookings() {
        List<Booking> bookingList = new ArrayList<>(List.of(new Booking(), new Booking()));

        when(bookingRepository.findAll()).thenReturn(bookingList);

        List<Booking> results = bookingService.getAllBookings();

        assertEquals(2, results.size());
        assertSame(bookingList, results);
    }

    @Test
    void getBookingsForBand_returnsMappedSummaries() {
        Booking booking1 = new Booking();
        booking1.setId(10L);

        Booking booking2 = new Booking();
        booking2.setId(20L);

        List<Booking> bookings = List.of(booking1, booking2);

        BookingSummaryDTO dto1 = summary(10L);
        BookingSummaryDTO dto2 = summary(20L);

        when(bookingRepository.findByBand_Id(2L)).thenReturn(bookings);
        when(bookingMapper.toBookingSummary(booking1)).thenReturn(dto1);
        when(bookingMapper.toBookingSummary(booking2)).thenReturn(dto2);

        List<BookingSummaryDTO> result = bookingService.getBookingsForBand(2L);

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

    }

    @Test
    void getBookingsForMusician_returnsMappedSummaries() {
        Booking booking1 = new Booking();
        booking1.setId(30L);

        Booking booking2 = new Booking();
        booking2.setId(40L);

        List<Booking> bookings = List.of(booking1, booking2);

        BookingSummaryDTO dto1 = summary(30L);
        BookingSummaryDTO dto2 = summary(40L);

        when(bookingRepository.findByMusicianList_Id(5L)).thenReturn(bookings);
        when(bookingMapper.toBookingSummary(booking1)).thenReturn(dto1);
        when(bookingMapper.toBookingSummary(booking2)).thenReturn(dto2);

        List<BookingSummaryDTO> results = bookingService.getBookingsForMusician(5L);

        assertEquals(2, results.size());
        assertSame(dto1, results.get(0));
        assertSame(dto2, results.get(1));
    }

    @Test
    void getBookingsForClient() {
        Booking booking1 = new Booking();
        booking1.setId(50L);

        Booking booking2 = new Booking();
        booking2.setId(60L);

        List<Booking> bookings = List.of(booking1, booking2);

        BookingSummaryDTO dto1 = summary(50L);
        BookingSummaryDTO dto2 = summary(60L);

        when(bookingRepository.findByClient_Id(70L)).thenReturn(bookings);
        when(bookingMapper.toBookingSummary(booking1)).thenReturn(dto1);
        when(bookingMapper.toBookingSummary(booking2)).thenReturn(dto2);

        List<BookingSummaryDTO> results = bookingService.getBookingsForClient(70L);

        assertEquals(2, results.size());
        assertSame(dto1, results.get(0));
        assertSame(dto2, results.get(1));

    }

    private BookingSummaryDTO summary(Long id) {
        return new BookingSummaryDTO(
                id,
                100L,
                "Client Name",
                200L,
                "Band Name",
                BookingStatus.Pending,
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(2)
        );
    }

}