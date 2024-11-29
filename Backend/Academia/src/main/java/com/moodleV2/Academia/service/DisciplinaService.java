package com.moodleV2.Academia.service;

import com.moodleV2.Academia.controllers.DisciplinaModelAssembler;
import com.moodleV2.Academia.exceptions.SearchParamException;
import com.moodleV2.Academia.models.*;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Page<Disciplina> DisciplinaSearch(Pageable pageable,
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

        List<Profesor> profesorList;
        if (numePrenumeTitular != null) {
            // this may really produce NullPointerException??
            String[] arr = numePrenumeTitular.split("\\s");
            String n = arr[0];
            String p = arr.length > 1 ? arr[1] : null;

            profesorList = profesorRepository.findAll(Specification.where(
                    numeContains(n)
                            .or(prenumeContains(p))
                            .and(arhivareEquals(fromArchive))
            ));
        }

        return disciplinaRepository.findAll(Specification.where(
                numeDisciplinaContains(nume)
                        .and(codEquals(cod))
                        .and(anStudiuEquals(anStudiu))
                        .and(tipDisciplinaEquals(tipDisciplina))
                        .and(tipExaminareEquals(tipExaminare))
                        .and(categorieEquals(categorie))
                        .and(arhivareEquals2(fromArchive))
                        // TODO: search by profesor logic
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

    public static Specification<Profesor> numeContains(String nume) {
        return (root, query, criteriaBuilder) ->
                nume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nume")), "%" + nume.toLowerCase() + "%");
    }

    public static Specification<Profesor> prenumeContains(String prenume) {
        return (root, query, criteriaBuilder) ->
                prenume == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("prenume")), "%" + prenume.toLowerCase() + "%");
    }

    public static Specification<Profesor> arhivareEquals(boolean arhivat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat);
    }
}
