package com.moodleV2.Academia.dto;

import com.moodleV2.Academia.models.Ciclu;
import com.moodleV2.Academia.models.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "DTO for creating or updating Student")
@Getter
public class StudentDto {

    @Schema(description = "Nume Student", example = "George", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    @Size(min = 1, max = 50)
    private String nume;

    @Schema(description = "Prenume Student", example = "Calinacio", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    @Size(min = 1, max = 50)
    private String prenume;

    @Schema(description = "Email Student", example = "andrei74c0@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    @Email
    private String email;

    @Schema(description = "Ciclul de studii", example = "LICENTA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    private Ciclu cicluStudii;

    @Schema(description = "An de studiu", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    @Min(1)
    @Max(5)
    private int anStudiu;

    @Schema(description = "Grupa", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    @Min(1)
    @Max(50)
    private int grupa;

    // DONE: implement discipline DTO
    @Schema(description = "Classes inrolled by the student")
    private Set<DisciplinaDto> classes;

    public StudentDto(String nume, String prenume, String email, Ciclu cicluStudii, int anStudiu, int grupa, Set<DisciplinaDto> classes) {
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.cicluStudii = cicluStudii;
        this.anStudiu = anStudiu;
        this.grupa = grupa;
        this.classes = classes;
    }

    public StudentDto(Student student) {
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
