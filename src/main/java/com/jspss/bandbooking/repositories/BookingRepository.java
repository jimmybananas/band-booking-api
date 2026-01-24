package com.jspss.bandbooking.repositories;

import com.jspss.bandbooking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBand_Id(Long bandId);
    List<Booking> findByMusicianList_Id(Long musicianId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.band.id = :bandId
        AND (
            (b.gigStarts <= :end AND b.gigEnds >= :start)
        )
    """)
    List<Booking> findConflictBookings(Long bandId, ZonedDateTime start, ZonedDateTime end);

    @Query("""
    SELECT b FROM Booking b
    JOIN b.musicianList m
    WHERE m.id = :id
      AND (b.gigStarts < :gigEnds AND b.gigEnds > :gigStarts)
""")
    List<Booking> findMusicianConflicts(Long id, ZonedDateTime gigStarts, ZonedDateTime gigEnds);

    List<Booking> findByClient_Id(Long id);
}
