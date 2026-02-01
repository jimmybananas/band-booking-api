package com.jspss.bandbooking.repositories;

import com.jspss.bandbooking.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("""
    SELECT c FROM Client c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<Client> searchClients(String query);

    List<Client> findByNameContainingIgnoreCase(String query);
}
