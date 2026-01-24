package com.jspss.bandbooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Musician {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @ManyToMany
    @JoinTable(
            name = "musician_instrument",
            joinColumns = @JoinColumn(name = "musician_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private List<Instrument> instruments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "musician_style",
            joinColumns = @JoinColumn(name = "musician_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id")
    )
    private List<MusicStyle> musicStyles = new ArrayList<>();

    @ManyToMany(mappedBy = "bandMembers")
    @JsonIgnore
    private List<Band> bands = new ArrayList<>();


    @ManyToMany(mappedBy = "musicianList")
    @JsonIgnore
    private List<Booking> bookingsList = new ArrayList<>();
}
