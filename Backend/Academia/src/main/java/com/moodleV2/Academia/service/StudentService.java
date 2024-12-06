package com.moodleV2.Academia.service;

import com.moodleV2.Academia.controllers.StudentModelAssembler;
import com.moodleV2.Academia.dto.*;
import com.moodleV2.Academia.exceptions.*;
import com.moodleV2.Academia.models.Ciclu;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.models.Student;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import com.moodleV2.Academia.repositories.StudentRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.moodleV2.Academia.service.DataValidators.emailValidator;
import static com.moodleV2.Academia.service.DataValidators.numeValidator;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentModelAssembler assembler;
    private final DisciplinaRepository disciplinaRepository;
    private final ProfesorRepository profesorRepository;

    public StudentService(StudentRepository studentRepository, StudentModelAssembler assembler, DisciplinaRepository disciplinaRepository, ProfesorRepository profesorRepository) {
        this.studentRepository = studentRepository;
        this.assembler = assembler;
        this.disciplinaRepository = disciplinaRepository;
        this.profesorRepository = profesorRepository;
    }

    public Page<Student> studentSearch(Pageable pageable, String nume, String prenume, String email, Ciclu ciclu, Integer an, Integer grupa, Long profId, String codDisciplina, boolean arhivat) {

        numeValidator(nume, 2, 50,
                () -> new SearchParamException(nume));
        numeValidator(prenume, 2, 50,
                () -> new SearchParamException(prenume));
        emailValidator(email,
                () -> new SearchParamException(email));
        if (an != null && (an < 1 || an > 4)) {
            throw new SearchParamException(an);
        }
        if (grupa != null && (grupa < 1 || grupa > 50)) {
            throw new SearchParamException(grupa);
        }
        if (codDisciplina != null && (codDisciplina.length() > 20 || codDisciplina.isBlank())) {
            throw new SearchParamException(codDisciplina);
        }
        if (profId != null && (profId > 1000 || profId < 1)) {
            throw new SearchParamException(profId);
        }

        // return all student ids that are enrolled in a course
        Set<Long> ids = new HashSet<>();
        if (codDisciplina != null) {
            Disciplina disciplina = disciplinaRepository.findById(codDisciplina)
                    .orElseThrow(() -> new DisciplinaNotFoundException(codDisciplina));
            if (disciplina.isArhivat() == arhivat) {
                ids = studentRepository.searchStudentsByClasses(disciplina)
                        .stream()
                        .filter(student -> student.isArhivat() == arhivat)
                        .map(Student::getId).collect(Collectors.toSet());
            }
            else {
                throw new DisciplinaNotFoundException(codDisciplina);
            }
        }

        if (profId != null) {
            Profesor profesor = profesorRepository.findById(profId)
                    .orElseThrow(() -> new ProfesorNotFoundException(profId));

            if (profesor.isArhivat() != arhivat) {
                throw new ProfesorNotFoundException(profId);
            }

            List<Disciplina> disciplinas = disciplinaRepository.findByIdTitular(profesor);
            for (Disciplina d : disciplinas) {
                    ids.addAll(studentRepository.getStudentsByClasses(d).stream()
                            .filter(student -> student.isArhivat() == arhivat)
                            .map(Student::getId).collect(Collectors.toSet()));
            }
        }

        if ((codDisciplina != null || profId != null) && ids.isEmpty()) {
            return Page.empty(pageable);
        }

        return studentRepository.findAll(Specification.where(
                nameContains(nume)
                        .and(prenumeContains(prenume))
                        .and(emailContains(email))
                        .and(cicluEquals(ciclu))
                        .and(anEquals(an))
                        .and(grupaEquals(grupa))
                        .and(arhivareEquals(arhivat))
                        .and(idsIn(ids))
                ), pageable);
    }

    public static Specification<Student> nameContains(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nume")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Student> prenumeContains(String prenume) {
        return (root, query, criteriaBuilder) ->
                prenume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("prenume")), "%" + prenume.toLowerCase() + "%");
    }

    public static Specification<Student> emailContains(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Student> cicluEquals(Ciclu ciclu){
        return (root, query, criteriaBuilder) ->
                ciclu == null ? null : criteriaBuilder.equal(root.get("cicluStudii"), ciclu);
    }

    public static Specification<Student> anEquals(Integer an) {
        return (root, query, criteriaBuilder) ->
                an == null ? null : criteriaBuilder.equal(root.get("anStudiu"), an);
    }

    public static Specification<Student> grupaEquals(Integer grupa) {
        return (root, query, criteriaBuilder) ->
                grupa == null ? null : criteriaBuilder.equal(root.get("grupa"), grupa);
    }

    public static Specification<Student> arhivareEquals(boolean arhivat) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat));
    }

    public static Specification<Student> idsIn(Set<Long> ids) {
        return (root, query, criteriaBuilder) ->
                ids == null || ids.isEmpty() ? null : root.get("id").in(ids);
    }
    
    public EntityModel<StudentDto> addStudent(StudentDtoCreateNew studentDto) {

        String nume = studentDto.getNume();
        String prenume = studentDto.getPrenume();
        String email = studentDto.getEmail();
        int an = studentDto.getAnStudiu();
        int grupa = studentDto.getGrupa();
        Set<String> classes = studentDto.getClasses();

        numeValidator(nume, 2, 50,
                () -> new InvalidFieldException("Last name { " + nume + " } is not valid."));
        numeValidator(prenume, 2, 50,
                () -> new InvalidFieldException("First name { " + prenume + " } is not valid."));

        emailValidator(email,
                () -> new InvalidFieldException("Email { " + email + " } is not valid."));
        if (studentRepository.existsStudentByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email {" + email + "} already exists.");
        }

        if (an < 1 || an > 4) {
            throw new InvalidFieldException("Year { " + an + " } is not valid.");
        }
        if (grupa < 1 || grupa > 50) {
            throw new InvalidFieldException("Group number { " + grupa + " } is not valid.");
        }

        Student student = new Student(
                nume,
                prenume,
                email,
                studentDto.getCicluStudii(),
                an,
                grupa
        );

        if (classes == null || classes.isEmpty()) {
            return assembler.toModel(studentRepository.save(student));
        }

        Set<Disciplina> discipline = disciplinaRepository.findAllById(classes).stream().filter(disciplina -> !disciplina.isArhivat()).collect(Collectors.toSet());

        if (discipline.isEmpty() || discipline.size() < classes.size()) {
            throw new DisciplinaNotFoundException("One or more of the specified course codes was not found.");
        }
        student.getClasses().addAll(discipline);

        return assembler.toModel(studentRepository.save(student));
    }

    public Student partialUpdateStudent(Long id, StudentDtoUpdate studentDto) {

        String nume = studentDto.getNume();
        String prenume = studentDto.getPrenume();
        String email = studentDto.getEmail();
        Integer an = studentDto.getAnStudiu();
        Integer grupa = studentDto.getGrupa();
        Set<String> classes = studentDto.getClasses();

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id { " + id + " } was not found."));

        if (student.isArhivat()) {
            throw new StudentNotFoundException("Student with id { " + id + " } was not found.");
        }

        if (nume != null) {
            numeValidator(nume, 2, 50,
                    () -> new InvalidFieldException("Last name { " + nume + " } is not valid."));
            student.setNume(nume);
        }

        if (prenume != null) {
            numeValidator(prenume, 2, 50,
                    () -> new InvalidFieldException("First name { " + prenume + " } is not valid."));
            student.setPrenume(prenume);
        }

        if (email != null) {
            emailValidator(email,
                    () -> new InvalidFieldException("Email { " + email + " } is not valid."));
            if (studentRepository.existsStudentByEmail(email)) {
                throw new ResourceAlreadyExistsException("Email {" + email + "} already exists.");
            }
            student.setEmail(email);
        }

        if (an != null) {
            if (an < 1 || an > 4) {
                throw new InvalidFieldException("Year { " + an + " } is not valid.");
            }
            student.setAnStudiu(an);
        }

        if (grupa != null) {
            if (grupa < 1 || grupa > 50) {
                throw new InvalidFieldException("Group number { " + grupa + " } is not valid.");
            }
            student.setGrupa(grupa);
        }

        if (classes == null || classes.isEmpty()) {
            studentRepository.save(student);
            return student;
        }

        Set<Disciplina> discipline = disciplinaRepository.findAllById(classes).stream().filter(disciplina -> !disciplina.isArhivat()).collect(Collectors.toSet());

        if (discipline.isEmpty() || discipline.size() < classes.size()) {
            throw new DisciplinaNotFoundException("One or more of the specified course codes was not found.");
        }
        student.getClasses().addAll(discipline);

        studentRepository.save(student);

        return student;
    }
}
