package com.moodleV2.Academia.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "studenti")
@Setter
@Getter
public class Student {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "nume", nullable = false)
    @Size(min = 1, max = 50)
    private String nume;

    @Column(name = "prenume", nullable = false)
    @Size(min = 1, max = 50)
    private String prenume;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "ciclu_studii", nullable = false)
    private Ciclu cicluStudii;

    @Column(name = "an_studii", nullable = false)
    @Min(1)
    @Max(5)
    private int anStudiu;

    @Column(name = "grupa", nullable = false)
    @Min(1)
    @Max(50)
    private int grupa;

    public Student() {}

    public Student(String nume, String prenume, String email, Ciclu cicluStudii, int anStudiu, int grupa) {
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.cicluStudii = cicluStudii;
        this.anStudiu = anStudiu;
        this.grupa = grupa;
    }

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "join_ds",
            inverseJoinColumns = @JoinColumn(name = "discipline_id"),
            joinColumns = @JoinColumn(name = "studenti_id"))
    private Set<Disciplina> classes = new HashSet<>();
}
