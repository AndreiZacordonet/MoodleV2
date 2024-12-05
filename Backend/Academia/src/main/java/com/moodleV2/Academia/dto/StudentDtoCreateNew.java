package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Ciclu;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Schema(description = "DTO for creating or updating Student")
@Getter
@Setter
public class StudentDtoCreateNew {

    @Schema(description = "Nume Student", example = "George", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 50)
    private String nume;

    @Schema(description = "Prenume Student", example = "Calinacio", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 50)
    private String prenume;

    @Schema(description = "Email Student", example = "andrei74c0@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Email
    private String email;

    @Schema(description = "Ciclul de studii", example = "LICENTA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Ciclu cicluStudii;

    @Schema(description = "An de studiu", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    @Max(5)
    private int anStudiu;

    @Schema(description = "Grupa", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    @Max(50)
    private int grupa;

    @Schema(description = "Classes enrolled by the student")
    private Set<String> classes;
}
