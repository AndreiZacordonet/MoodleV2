package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Categorie;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.TipDisciplina;
import com.moodleV2.Academia.models.TipExaminare;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(description = "DTO for creating Disciplina")
@Getter
public class DisciplinaDtoCreateNew {
    @Schema(description = "Discipline code", example = "MATH69", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 20)
    private String cod;

    @Schema(description = "Titularul disciplinei", example = "pingu@cimiceanga.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email
    private String emailTitular;

    @Schema(description = "Numele disciplinei", example = "Matematica amuzanta", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 2, max = 100)
    private String numeDisciplina;

    @Schema(description = "Anul in care se studiaza disciplina", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(1)
    @Max(5)
    private int anStudiu;

    @Schema(description = "Tipul disciplinei", example = "IMPUSA", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipDisciplina tipDisciplina;

    @Schema(description = "Categoria disciplinei", example = "SPECIALITATE", requiredMode = Schema.RequiredMode.REQUIRED)
    private Categorie categorie;

    @Schema(description = "Tipul examinarii", example = "COLOCVIU", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipExaminare tipExaminare;

}
