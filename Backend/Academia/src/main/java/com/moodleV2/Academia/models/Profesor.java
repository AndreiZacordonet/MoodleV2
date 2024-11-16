package com.moodleV2.Academia.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cadre_didactice")
@Setter
@Getter
public class Profesor {

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
    @Column(name = "grad_didactic")
    private Grad gradDidactic;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_asociere", nullable = false)
    private Asociere tipAsociere;

    @Column(name = "afiliere")
    @Size(max = 50)
    private String afiliere;

    public Profesor() {}

    public Profesor(String nume, String prenume, String email, Grad gradDidactic, Asociere tipAsociere, String afiliere) {
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.gradDidactic = gradDidactic;
        this.tipAsociere = tipAsociere;
        this.afiliere = afiliere;
    }

    @Override
    public String toString() {
        return "Profesor:{\nid=" + this.id + "\nnume=" + this.nume + "\nprenume=" + this.prenume + "\nemail=" + this.email
                + "\ngrad=" +this.gradDidactic + "\nasociere=" + this.tipAsociere + "\nafiliere=" + this.afiliere + "\n}\n";
    }

}
