package com.jspss.bandbooking.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bandName;

    @ManyToMany
    @JoinTable(
            name = "band_style",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "musicstyle_id")
    )
    private List<MusicStyle> musicStyles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "band_members",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "musician_id")
    )
    @JsonIgnore
    private List<Musician> bandMembers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "band_required_instruments",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    @JsonIgnore
    private List<Instrument> requiredInstruments = new ArrayList<>();


}
