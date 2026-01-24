package com.jspss.bandbooking.entities;

import com.jspss.bandbooking.entities.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band band;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @ManyToMany
    @JoinTable(
            name = "booking_musicians",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "musician_id")
    )
    private List<Musician> musicianList = new ArrayList<>();

    @Column(nullable = false)
    private ZonedDateTime gigStarts;

    @Column(nullable = false)
    private ZonedDateTime gigEnds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;


}
