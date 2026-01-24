package com.jspss.bandbooking.repositories;

import com.jspss.bandbooking.entities.Band;
import com.jspss.bandbooking.entities.Musician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {
    Long id(Long id);
    List<Band> searchByName(String query);
    List<Band> findByBandNameContainingIgnoreCase(String query);
    boolean existsByNameIgnoreCase(String query);

}
