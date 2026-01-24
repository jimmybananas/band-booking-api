package com.jspss.bandbooking.repositories;

import com.jspss.bandbooking.entities.Instrument;
import com.jspss.bandbooking.entities.MusicStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
    List<Instrument> id(Long id);
    boolean existsByNameIgnoreCase(String string);
    List<Instrument> findByNameContainingIgnoreCase(String query);

    @Query(value = """
        SELECT * FROM instrument
        WHERE name % :query
        ORDER BY similarity(name, :query)
    """, nativeQuery = true)
    List<MusicStyle> fuzzySearch(@Param("query") String query);
}
