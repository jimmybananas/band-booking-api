package com.jspss.bandbooking.repositories;

import com.jspss.bandbooking.entities.MusicStyle;
import com.jspss.bandbooking.entities.Musician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MusicianRepository extends JpaRepository<Musician, Long> {

    List<Musician> findByBands_Id(Long bandId);
    List<Musician> findByInstruments_Id(Long instrumentId);
    List<Musician> findByMusicStyles_Id(Long styleId);
    List<Musician> findByFull_Name(String fullName);



    @Query("""
        SELECT m FROM Musician m
        JOIN m.bands b
        WHERE b.id = :bandId
            AND m.id NOT IN (
            SELECT m2.id FROM Booking bk
            JOIN bk.musicianList m2
            WHERE bk.bookingStatus <> 'Cancelled'
                AND (bk.gigStarts < :end AND bk.gigEnds > :start)
            )
    """)
    List<Musician> findAvailableMusicians(@Param("bandId") Long bandId,
                                          @Param("start")ZonedDateTime start,
                                          @Param("end")ZonedDateTime end);

    @Query("""
    SELECT m FROM Musician m
    JOIN m.bands b
    JOIN m.instruments i
    WHERE b.id = :bandId
      AND i.id = :instrumentId
      AND m.id NOT IN (
            SELECT m2.id FROM Booking bk
            JOIN bk.musicianList m2
            WHERE bk.bookingStatus <> 'Cancelled'
              AND (bk.gigStarts < :end AND bk.gigEnds > :start)
      )
    """)
    List<Musician> findAvailableMusiciansForInstrument(
            @Param("bandId") Long bandId,
            @Param("instrumentId") Long instrumentId,
            @Param("start")ZonedDateTime start,
            @Param("end")ZonedDateTime end
    );

    @Query(value= """
                SELECT * FROM musician
                WHERE full_name % :query
                ORDER BY similarity(full_name, :query) DESC LIMIT (20)
    """, nativeQuery = true)
    List<Musician> fuzzySearch(@Param("query") String query);

}
