package com.moodleV2.Academia.service;

import com.moodleV2.Academia.controllers.DisciplinaModelAssembler;
import com.moodleV2.Academia.controllers.ProfesorModelAssembler;
import com.moodleV2.Academia.controllers.StudentModelAssembler;
import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.dto.StudentDto;
import com.moodleV2.Academia.exceptions.InvalidFieldException;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.exceptions.ResourceAlreadyExistsException;
import com.moodleV2.Academia.exceptions.SearchParamException;
import com.moodleV2.Academia.models.*;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import com.moodleV2.Academia.repositories.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.moodleV2.Academia.service.DataValidators.emailValidator;
import static com.moodleV2.Academia.service.DataValidators.numeValidator;


@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final StudentRepository studentRepository;
    private final ProfesorModelAssembler assembler;
    private final DisciplinaModelAssembler assemblerDisciplina;
    private final StudentModelAssembler assemblerStudent;

    public ProfesorService(ProfesorRepository profesorRepository, DisciplinaRepository disciplinaRepository, StudentRepository studentRepository, ProfesorModelAssembler assembler, DisciplinaModelAssembler assemblerDisciplina, StudentModelAssembler assemblerStudent) {
        this.profesorRepository = profesorRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.studentRepository = studentRepository;
        this.assembler = assembler;
        this.assemblerDisciplina = assemblerDisciplina;
        this.assemblerStudent = assemblerStudent;
    }

    public Page<Profesor> ProfesorSearch(Pageable pageable,
                                         String nume, String prenume, String email, Grad grad, Asociere asociere, String codDisciplina, String numeDisciplina, boolean fromArhive) {

        numeValidator(nume, 2, 50,
                () -> new SearchParamException("Professor last name search parameter { " + nume + " } is not valid."));
        numeValidator(prenume, 2, 50,
                () -> new SearchParamException("Professor first name search parameter { " + prenume + " } is not valid."));

        emailValidator(email,
                () -> new SearchParamException("Professor email search parameter { " + email + " } is not valid."));
        if (profesorRepository.existsProfesorByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email { " + email + " } already exists.");
        }
        if (codDisciplina != null && (codDisciplina.length() > 20 || codDisciplina.isBlank())) {
            throw new SearchParamException(codDisciplina);
        }
        numeValidator(numeDisciplina, 1, 100,
                () -> new SearchParamException("Course code search parameter {" + numeDisciplina + "} is not valid."));

        List<Long> idList = null;
        if (codDisciplina != null || numeDisciplina != null) {
            List<Disciplina> disciplinaList = disciplinaRepository.findAll(Specification.where(
                    codEquals(codDisciplina)
                            .or(numeContains(numeDisciplina))
                            .and(arhivareEquals2(fromArhive))
            ));

            // get profesor id from a discipline
            idList = disciplinaList.stream()
                    .map(disciplina -> {
                        if (fromArhive) {
                            return disciplina.getIdTitular().isArhivat() ? disciplina.getIdTitular().getId() : null;
                        }
                        return disciplina.getIdTitular().isArhivat() ? null : disciplina.getIdTitular().getId();
                    }).toList();
        }

        if ((codDisciplina != null || numeDisciplina != null) && idList.isEmpty()) {
            return Page.empty(pageable);
        }
        return profesorRepository.findAll(Specification.where(
                nameContains(nume)
                        .and(prenumeContains(prenume))
                        .and(emailContains(email))
                        .and(gradEquals(grad))
                        .and(asociareEquals(asociere))
                        .and(arhivareEquals(fromArhive))
                        .and(idsIn(idList))
        ), pageable);
    }

    public static Specification<Profesor> nameContains(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nume")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Profesor> prenumeContains(String prenume) {
        return (root, query, criteriaBuilder) ->
                prenume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("prenume")), "%" + prenume.toLowerCase() + "%");
    }

    public static Specification<Profesor> emailContains(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Profesor> gradEquals(Grad grad) {
        return (root, query, criteriaBuilder) ->
                grad == null ? null : criteriaBuilder.equal(root.get("gradDidactic"), grad);
    }

    public static Specification<Profesor> asociareEquals(Asociere asociere) {
        return (root, query, criteriaBuilder) ->
                asociere == null ? null : criteriaBuilder.equal(root.get("tipAsociere"), asociere);
    }

    public static Specification<Profesor> idsIn(List<Long> ids) {
        return (root, query, criteriaBuilder) ->
                ids == null || ids.isEmpty() ? null : root.get("id").in(ids);
    }

    public static Specification<Student> idsIn2(List<Long> ids) {
        return (root, query, criteriaBuilder) ->
                ids == null || ids.isEmpty() ? null : root.get("id").in(ids);
    }

    public static Specification<Disciplina> codEquals(String cod) {
        return (root, query, criteriaBuilder) ->
                cod == null ? null : criteriaBuilder.equal(root.get("cod"), cod);
    }

    public static Specification<Disciplina> numeContains(String nume) {
        return (root, query, criteriaBuilder) ->
                nume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("numeDisciplina")), "%" + nume.toLowerCase() + "%");
    }

    public static Specification<Disciplina> arhivareEquals2(boolean arhivat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat);
    }

    // pentru selectarea profesorilor arhivati
    public static Specification<Profesor> arhivareEquals(boolean arhivat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat);
    }

    public EntityModel<ProfesorDto> AddProfesor(ProfesorDto profesorDto) {

        String nume = profesorDto.getNume();
        String prenume = profesorDto.getPrenume();
        String email = profesorDto.getEmail();
        String afiliere = profesorDto.getAfiliere();

        numeValidator(nume, 2, 50,
                () -> new InvalidFieldException("Last name { " + nume + " } is not valid."));
        numeValidator(prenume, 2, 50,
                () -> new InvalidFieldException("First name { " + prenume + " } is not valid."));

        emailValidator(email,
                () -> new InvalidFieldException("Email { " + email + " } is not valid."));
        if (profesorRepository.existsProfesorByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email {" + email + "} already exists.");
        }
        if (afiliere.length() > 100) {
            throw new InvalidFieldException("Affiliation { " + afiliere + " } is not valid.");
        }

        return assembler.toModel(profesorRepository.save(profesorDto.ProfesorMapper()));
    }

    public Profesor PartialUpdateProfesor(Long id, Map<String, String> fields) {

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        // daca profesorul este arhivat acesta nu va fi returnat
        if (profesor.isArhivat()) {
            throw new ProfesorNotFoundException(id);
        }

        fields.forEach((fieldName, value) -> {
            switch (fieldName) {
                case "nume" -> {
                    String nume = value;
                    numeValidator(nume, 2, 50,
                            () -> new InvalidFieldException("Last name { " + nume + " } is not valid."));
                    profesor.setNume(nume);
                }
                case "prenume" -> {
                    String prenume = value;
                    numeValidator(prenume, 2, 50,
                            () -> new InvalidFieldException("First name { " + prenume + " } is not valid."));
                    profesor.setPrenume(prenume);
                }
                case "email" -> {
                    String email = value;
                    emailValidator(email,
                            () -> new InvalidFieldException("Email { " + email + " } is not valid."));
                    if (profesorRepository.existsProfesorByEmail(email)) {
                        throw new ResourceAlreadyExistsException("Email { " + email + " } already exists.");
                    }
                    profesor.setEmail(email);
                }
                case "gradDidactic" -> {
                    String gradValue = value;
                    try {
                        Grad gradDidactic = Grad.valueOf(gradValue.toUpperCase());
                        profesor.setGradDidactic(gradDidactic);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("Professor didactic grade { " + gradValue + " } is not valid.");
                    }
                }
                case "tipAsociere" -> {
                    String asociereValue = value;
                    try {
                        Asociere tipAsociere = Asociere.valueOf(asociereValue.toUpperCase());
                        profesor.setTipAsociere(tipAsociere);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("Professor association type { " + asociereValue + " } is not valid.");
                    }
                }
                case "afiliere" -> {
                    String afiliere = value;
                    if (afiliere.length() > 100) {
                        throw new InvalidFieldException("Affiliation { " + afiliere + " } is not valid.");
                    }
                    profesor.setAfiliere(afiliere);
                }
                default -> throw new InvalidFieldException("Field { " + fieldName + "} is not valid.");
            }
        });

        profesorRepository.save(profesor);

        return profesor;
    }

    public List<EntityModel<DisciplinaDto>> getMyDisciplines(Long id) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        if (profesor.isArhivat()) {
            throw new ProfesorNotFoundException(id);
        }

        // FIXME: add pagination
        return disciplinaRepository.findByIdTitular(profesor)
                .stream()
                .filter(disciplina -> !disciplina.isArhivat())
                .map(assemblerDisciplina::toModel)
                .toList();
    }

    public List<EntityModel<DisciplinaDto>> getAllDisciplines(Long id) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }
        // FIXME: if teacher is logged in?
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        if (profesor.isArhivat()) {
            throw new ProfesorNotFoundException(id);
        }

        // FIXME: add pagination
        // TODO: add link
        return disciplinaRepository.findAll()
                .stream()
                .filter(disciplina -> !disciplina.isArhivat())
                .map(disciplina -> {
                        EntityModel<DisciplinaDto> entityModel = assemblerDisciplina.toModel(disciplina);
                        entityModel.add(Link.of("/api/academia/discipline").withRel("discipline").withType("GET"));

                        return entityModel;
                })
                .toList();
    }

    public List<EntityModel<StudentDto>> getMyStudents(Long id) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        if (profesor.isArhivat()) {
            throw new ProfesorNotFoundException(id);
        }

        // FIXME: not working properly
        // FIXME: add pagination
        Set<Long> ids = new HashSet<>();
        List<Disciplina> disciplinas = disciplinaRepository.findByIdTitular(profesor);
        for (Disciplina d : disciplinas) {
            ids.addAll(studentRepository.getStudentsByClasses(d).stream()
                    .filter(student -> !student.isArhivat())
                    .map(Student::getId).collect(Collectors.toSet()));
        }

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return studentRepository.findAll(Specification.where(idsIn2(ids.stream().toList())))
                .stream().map(assemblerStudent::toModel).toList();
    }

}
