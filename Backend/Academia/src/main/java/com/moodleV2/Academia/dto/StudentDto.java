package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Ciclu;
import com.moodleV2.Academia.models.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "DTO for creating or updating Student")
@Getter
@Setter
public class StudentDto {

    @Schema(description = "Id Student", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private Long id;

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
    private Set<DisciplinaDto> classes;

    public StudentDto(Student student) {
        this.id = student.getId();
        this.nume = student.getNume();
        this.prenume = student.getPrenume();
        this.email = student.getEmail();
        this.cicluStudii = student.getCicluStudii();
        this.anStudiu = student.getAnStudiu();
        this.grupa = student.getGrupa();
        this.classes = student.getClasses().stream().map(disciplina -> new DisciplinaDto(
                disciplina.getCod(),
                new ProfesorDto(disciplina.getIdTitular()),
                disciplina.getNumeDisciplina(),
                disciplina.getAnStudiu(),
                disciplina.getTipDisciplina(),
                disciplina.getCategorie(),
                disciplina.getTipExaminare()
        )).collect(Collectors.toSet());
    }

    public Student StudentMapper() {
        Student student = new Student(
                this.nume,
                this.prenume,
                this.email,
                this.cicluStudii,
                this.anStudiu,
                this.grupa
        );
        if (this.classes != null) {
            student.getClasses().addAll(this.classes.stream().map(DisciplinaDto::DisciplinaMapper).collect(Collectors.toSet()));
        }

        return student;
    }
}
