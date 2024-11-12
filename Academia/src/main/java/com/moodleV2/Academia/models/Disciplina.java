package com.moodleV2.Academia.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discipline")
@Setter
@Getter
public class Disciplina {

    @Id
    @Size(max = 20)
    private String cod;

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor idTitular;

    @Column(name = "nume_disciplina", nullable = false)
    @Size(max = 100)
    private String numeDisciplina;

    @Column(name = "an_studiu", nullable = false)
    @Min(1)
    @Max(4)
    private int anStudiu;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_disciplina", nullable = false)
    private TipDisciplina tipDisciplina;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_examinare", nullable = false)
    private TipExaminare tipExaminare;

    public Disciplina() {}

    public Disciplina(String cod, Profesor idTitular, String numeDisciplina, int anStudiu, TipDisciplina tipDisciplina, Categorie categorie, TipExaminare tipExaminare) {
        this.cod = cod;
        this.idTitular = idTitular;
        this.numeDisciplina = numeDisciplina;
        this.anStudiu = anStudiu;
        this.tipDisciplina = tipDisciplina;
        this.categorie = categorie;
        this.tipExaminare = tipExaminare;
    }
}
