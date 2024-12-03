package com.moodleV2.Academia.service;

import com.moodleV2.Academia.controllers.DisciplinaModelAssembler;
import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.dto.DisciplinaDtoCreateNew;
import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.exceptions.DisciplinaNotFoundException;
import com.moodleV2.Academia.exceptions.InvalidFieldException;
import com.moodleV2.Academia.exceptions.SearchParamException;
import com.moodleV2.Academia.models.*;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.util.List;
import java.util.Map;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaModelAssembler assembler;
    private final ProfesorRepository profesorRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, DisciplinaModelAssembler assembler, ProfesorRepository profesorRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.assembler = assembler;
        this.profesorRepository = profesorRepository;
    }

    public Page<Disciplina> disciplinaSearch(Pageable pageable,
                                             String cod, String nume, Integer anStudiu, TipDisciplina tipDisciplina, Categorie categorie, TipExaminare tipExaminare, String numePrenumeTitular, String emailTitular, boolean fromArchive) {

        // validari
        if (cod != null && cod.length() > 20) {
            throw new SearchParamException(cod);
        }
        if (nume != null && nume.length() > 100) {
            throw new SearchParamException(nume);
        }
        if (anStudiu != null && (anStudiu > 5 || anStudiu < 1)) {
            throw new SearchParamException(anStudiu);
        }

        EmailValidator emailValidator = new EmailValidator();
        if (emailTitular != null && (emailTitular.length() > 50 || !emailValidator.isValid(emailTitular, null))) {
            throw new SearchParamException(emailTitular);
        }
        if (numePrenumeTitular != null && numePrenumeTitular.length() > 100) {
            throw new SearchParamException(numePrenumeTitular);
        }

        List<Profesor> profesorList = null;
        String n = null, p = null;
        if (numePrenumeTitular != null && !numePrenumeTitular.isBlank()) {
            //
            String[] numePrenume = numePrenumeTitular.split("\\s");
            n = numePrenume[0];
            p = numePrenume.length > 1 ? numePrenume[1] : null;
        }
        profesorList = profesorRepository.findAll(Specification.where(
                numeContains(n)
                        .or(prenumeContains(p))
                        .and(emailContains(emailTitular))
//                        .and(arhivareEquals(fromArchive))
        ));
        if (profesorList.isEmpty() && (n != null || p != null || emailTitular != null)) {
            return Page.empty(pageable);
        }

        return disciplinaRepository.findAll(Specification.where(
                numeDisciplinaContains(nume)
                        .and(codEquals(cod))
                        .and(anStudiuEquals(anStudiu))
                        .and(tipDisciplinaEquals(tipDisciplina))
                        .and(tipExaminareEquals(tipExaminare))
                        .and(categorieEquals(categorie))
                        .and(arhivareEquals2(fromArchive))
                        .and(profesorIdEquals(profesorList))
        ), pageable);

    }

    public static Specification<Disciplina> codEquals(String cod) {
        return (root, query, criteriaBuilder) ->
                cod == null ? null : criteriaBuilder.equal(root.get("cod"), cod);
    }

    public static Specification<Disciplina> anStudiuEquals(Integer anStudiu) {
        return (root, query, criteriaBuilder) ->
                anStudiu == null ? null : criteriaBuilder.equal(root.get("anStudiu"), anStudiu.longValue());
    }

    public static Specification<Disciplina> numeDisciplinaContains(String numeDisciplina) {
        return (root, query, criteriaBuilder) ->
                numeDisciplina == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("numeDisciplina")), "%" + numeDisciplina.toLowerCase() + "%");
    }

    public static Specification<Disciplina> tipDisciplinaEquals(TipDisciplina tipDisciplina) {
        return (root, query, criteriaBuilder) ->
                tipDisciplina == null ? null : criteriaBuilder.equal(root.get("tipDisciplina"), tipDisciplina);
    }

    public static Specification<Disciplina> tipExaminareEquals(TipExaminare tipExaminare) {
        return (root, query, criteriaBuilder) ->
                tipExaminare == null ? null : criteriaBuilder.equal(root.get("tipExaminare"), tipExaminare);
    }

    public static Specification<Disciplina> categorieEquals(Categorie categorie) {
        return (root, query, criteriaBuilder) ->
                categorie == null ? null : criteriaBuilder.equal(root.get("categorie"), categorie);
    }

    public static Specification<Disciplina> arhivareEquals2(boolean arhivat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat);
    }

    public static Specification<Disciplina> profesorIdEquals(List<Profesor> profesorList) {
        if (profesorList == null || profesorList.isEmpty()) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        }

        List<Long> profesorIds = profesorList.stream()
                .map(Profesor::getId)
                .toList();

        return (root, query, criteriaBuilder) -> root.get("idTitular").get("id").in(profesorIds);
    }

    public static Specification<Profesor> numeContains(String nume) {
        return (root, query, criteriaBuilder) ->
                nume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nume")), "%" + nume.toLowerCase() + "%");
    }

    public static Specification<Profesor> prenumeContains(String prenume) {
        return (root, query, criteriaBuilder) ->
                prenume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("prenume")), "%" + prenume.toLowerCase() + "%");
    }

    public static Specification<Profesor> emailContains(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Profesor> arhivareEquals(boolean arhivat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat);
    }

    public EntityModel<DisciplinaDto> addDisciplina(DisciplinaDtoCreateNew disciplinaDto) {

        String cod = disciplinaDto.getCod();
        String nume = disciplinaDto.getNumeDisciplina();
        String emailTitular = disciplinaDto.getEmailTitular();
        int anStudiu = disciplinaDto.getAnStudiu();
        TipDisciplina tipDisciplina = disciplinaDto.getTipDisciplina();
        Categorie categorie = disciplinaDto.getCategorie();
        TipExaminare tipExaminare = disciplinaDto.getTipExaminare();

        if (cod.length() > 20 || cod.isEmpty()) {
            throw new InvalidFieldException("Codul este invalid");
        }
        if (nume.length() > 100 || nume.length() < 2) {
            throw new InvalidFieldException("Numele disciplinei este invalid");
        }
        if (anStudiu < 1 || anStudiu > 5) {
            throw new InvalidFieldException("Anul de studiul al disciplinei este invalid.");
        }
        EmailValidator emailValidator = new EmailValidator();
        if (emailTitular.length() > 50 || !emailValidator.isValid(emailTitular, null)) {
            throw new InvalidFieldException("Emailul este invalid");
        }

        Profesor profesor = profesorRepository.findFirstByEmail(emailTitular);

        if (profesor == null) {
            throw new InvalidFieldException("Profesorul cu emailul {" + emailTitular + "} nu a fost gasit");
        }

        return assembler.toModel(disciplinaRepository.save(new Disciplina(
                cod,
                profesor,
                nume,
                anStudiu,
                tipDisciplina,
                categorie,
                tipExaminare
        )));
    }

    public Disciplina partialUpdateDisciplina(String cod, Map<String, String> fields) {

        Disciplina disciplina = disciplinaRepository.findById(cod)
                .orElseThrow(() -> new DisciplinaNotFoundException(cod));

        if (disciplina.isArhivat()) {
            throw new DisciplinaNotFoundException(cod);
        }

        fields.forEach((fieldName, value) -> {
            switch (fieldName) {
                case "nume" -> {
                    if (value.length() > 20 || value.isEmpty()) {
                        throw new InvalidFieldException("Codul este invalid");
                    }
                    disciplina.setNumeDisciplina(value);
                }
                case "emailTitular" -> {
                    EmailValidator emailValidator = new EmailValidator();
                    if (value.length() > 50 || !emailValidator.isValid(value, null)) {
                        throw new InvalidFieldException("Emailul este invalid");
                    }
                    Profesor profesor = profesorRepository.findFirstByEmail(value);

                    if (profesor == null) {
                        throw new InvalidFieldException("Profesorul cu emailul {" + value + "} nu a fost gasit");
                    }

                    disciplina.setIdTitular(profesor);
                }
                case "anStudiu" -> {
                    try {
                        int an = Integer.parseInt(value);
                        if (an < 1 || an > 5) {
                            throw new InvalidFieldException("Anul de studiul al disciplinei este invalid.");
                        }
                        disciplina.setAnStudiu(an);
                    }
                    catch (RuntimeException exception) {
                        throw new InvalidFieldException("Anul de studiul al disciplinei este invalid.");
                    }
                }
                case "tipDisciplina" -> {
                    try {
                        TipDisciplina tipDisciplina = TipDisciplina.valueOf(value.toUpperCase());
                        disciplina.setTipDisciplina(tipDisciplina);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("Tipul disciplinei este invalid");
                    }
                }
                case "categorie" -> {
                    try {
                        Categorie categorie = Categorie.valueOf(value.toUpperCase());
                        disciplina.setCategorie(categorie);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("Categoria este invalida");
                    }
                }
                case "tipExaminare" -> {
                    try {
                        TipExaminare tipExaminare = TipExaminare.valueOf(value.toUpperCase());
                        disciplina.setTipExaminare(tipExaminare);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("Tipul examinarii este invalid");
                    }
                }
                default -> throw new InvalidFieldException("Câmpul " + fieldName + " nu este valid");
            }
        });

        disciplinaRepository.save(disciplina);

        return disciplina;
    }
}
