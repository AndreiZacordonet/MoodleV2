package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Categorie;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.TipDisciplina;
import com.moodleV2.Academia.models.TipExaminare;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO for creating or updating Disciplina")
@Getter
@Setter
public class DisciplinaDto {

    @Schema(description = "Discipline code", example = "MATH69", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 20)
    private String cod;

    @Schema(description = "Titularul disciplinei", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private ProfesorDto titular;

    @Schema(description = "Numele disciplinei", example = "Matematica amuzanta", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 2, max = 100)
    private String numeDisciplina;

    @Schema(description = "Anul in care se studiaza disciplina", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    @Max(4)
    private int anStudiu;

    @Schema(description = "Tipul disciplinei", example = "IMPUSA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private TipDisciplina tipDisciplina;

    @Schema(description = "Categoria disciplinei", example = "SPECIALITATE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Categorie categorie;

    @Schema(description = "Tipul examinarii", example = "COLOCVIU", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipExaminare tipExaminare;

    public DisciplinaDto(String cod, ProfesorDto titular, String numeDisciplina, int anStudiu, TipDisciplina tipDisciplina, Categorie categorie, TipExaminare tipExaminare) {
        this.cod = cod;
        this.titular = titular;
        this.numeDisciplina = numeDisciplina;
        this.anStudiu = anStudiu;
        this.tipDisciplina = tipDisciplina;
        this.categorie = categorie;
        this.tipExaminare = tipExaminare;
    }

    public Disciplina DisciplinaMapper() {
        return new Disciplina(
                this.cod,
                this.titular.ProfesorMapper(),
                this.numeDisciplina,
                this.anStudiu,
                this.tipDisciplina,
                this.categorie,
                this.tipExaminare
        );
    }

}
