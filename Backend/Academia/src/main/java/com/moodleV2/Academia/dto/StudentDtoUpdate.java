package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Ciclu;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Schema(description = "DTO for updating Student")
@Getter
@Setter
public class StudentDtoUpdate {

    @Schema(description = "Nume Student", example = "George")
    @Size(min = 1, max = 50)
    private String nume;

    @Schema(description = "Prenume Student", example = "Calinacio")
    @Size(min = 1, max = 50)
    private String prenume;

    @Schema(description = "Email Student", example = "andrei74c0@gmail.com")
    @Email
    private String email;

    @Schema(description = "Ciclul de studii", example = "LICENTA")
    private Ciclu cicluStudii;

    @Schema(description = "An de studiu", example = "2")
    @Min(1)
    @Max(4)
    private Integer anStudiu;

    @Schema(description = "Grupa", example = "9")
    @Min(1)
    @Max(50)
    private Integer grupa;

    @Schema(description = "Classes enrolled by the student")
    private Set<String> classes;
}
