package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String imeProjekta;
    @Getter @Setter
    private String opisProjekta;
    @Getter @Setter
    private int brojLjudi;
    @Getter @Setter
    private LocalDate datumPoc;
    @Getter @Setter
    private LocalDate datumKraj;
    @Getter @Setter
    private String vrstaAktivnosti;
    @Getter @Setter
    private boolean jeLiHitno;
    @Getter @Setter
    private String ownerId;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProject> applicants = new ArrayList<>();

}
