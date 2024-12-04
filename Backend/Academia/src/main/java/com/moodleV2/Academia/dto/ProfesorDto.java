package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO for creating or updating Profesor")
@Getter
@Setter
public class ProfesorDto {

    @Schema(description = "Id Profesor", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nume Profesor", example = "Zaco", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Professor's last name MUST NOT be null.")
    @Size(min = 1, max = 50)
    private String nume;

    @Schema(description = "Prenume Profesor", example = "Andrei", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Professor's first name MUST NOT be null.")
    @Size(min = 1, max = 50)
    private String prenume;

    @Schema(description = "Email Profesor", example = "andrei74c0@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Professor's email MUST NOT be null.")
    @Email
    private String email;

    @Schema(description = "Grad Profesor", example = "ASISTENT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Professor's didactic grade MUST NOT be null.")
    private Grad gradDidactic;

    @Schema(description = "Asociere Profesor", example = "TITULAR", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Professor's association MUST NOT be null.")
    private Asociere tipAsociere;

    @Schema(description = "Afiliere Profesor", example = "afiliere maxima")
    @Size(max = 50)
    private String afiliere;

    public ProfesorDto() {}

    public ProfesorDto(Profesor profesor) {
        this.id = profesor.getId();
        this.nume = profesor.getNume();
        this.prenume = profesor.getPrenume();
        this.email = profesor.getEmail();
        this.gradDidactic = profesor.getGradDidactic();
        this.tipAsociere = profesor.getTipAsociere();
        this.afiliere = profesor.getAfiliere();
    }

    public Profesor ProfesorMapper() {
        return new Profesor(
                this.nume,
                this.prenume,
                this.email,
                this.gradDidactic,
                this.tipAsociere,
                this.afiliere
        );
    }

    @Override
    public String toString() {
        return "Profesor:{" + "\nnume=" + this.nume + "\nprenume=" + this.prenume + "\nemail=" + this.email
                + "\ngrad=" +this.gradDidactic + "\nasociere=" + this.tipAsociere + "\nafiliere=" + this.afiliere + "\n}\n";
    }
}
