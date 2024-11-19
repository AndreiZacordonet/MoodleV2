package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;

@Schema(description = "DTO for creating or updating Profesor")
@Getter
public class ProfesorDto {

    @Schema(description = "Nume Profesor", example = "Zaco", required = true)
    @NonNull
    @Size(min = 1, max = 50)
    private String nume;

    @Schema(description = "Prenume Profesor", example = "Andrei", required = true)
    @NonNull
    @Size(min = 1, max = 50)
    private String prenume;

    @Schema(description = "Email Profesor", example = "andrei74c0@gmail.com", required = true)
    @NonNull
    @Email
    private String email;

    @Schema(description = "Grad Profesor", example = "ASISTENT", required = true
//            allowableValues = {"ASISTENT", "SEF_LUCRARI", "CONFERENTIAR", "PROFESOR"}
    )
    @NonNull
    private Grad gradDidactic;

    @Schema(description = "Asociere Profesor", example = "TITULAR", required = true
//            allowableValues = {"TITULAR", "ASOCIAT", "EXTERN"}
    )
    @NonNull
    private Asociere tipAsociere;

    @Schema(description = "Afiliere Profesor", example = "afiliere amxima", required = false)
    @NonNull
    @Size(max = 50)
    private String afiliere;

    public ProfesorDto() {}

    public ProfesorDto(String nume, String prenume, String email, Grad gradDidactic, Asociere tipAsociere, String afiliere) {
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.gradDidactic = gradDidactic;
        this.tipAsociere = tipAsociere;
        this.afiliere = afiliere;
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
