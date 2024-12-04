package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Categorie;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.TipDisciplina;
import com.moodleV2.Academia.models.TipExaminare;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO for creating Disciplina")
@Getter
@Setter
public class DisciplinaDtoCreateNew {
    @Schema(description = "Discipline code", example = "MATH69", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 20)
    private String cod;

    @Schema(description = "Titularul disciplinei", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long idTitular;

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
    @NotNull
    private TipExaminare tipExaminare;

}
