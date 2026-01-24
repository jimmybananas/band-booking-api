package com.jspss.bandbooking.repositories;

import com.jspss.bandbooking.entities.MusicStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicStyleRepository extends JpaRepository<MusicStyle, Long> {
    List<MusicStyle> findByNameContainingIgnoreCase(String query);
    boolean existsByNameIgnoreCase(String string);

    @Query(value = """
             SELECT * FROM music_style
             WHERE name % :query
             ORDER BY similarity(name, :query)
    """, nativeQuery = true)
    List<MusicStyle> fuzzySearch(@Param("query") String query);
}
