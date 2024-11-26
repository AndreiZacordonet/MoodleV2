package com.moodleV2.Academia.service;

import com.moodleV2.Academia.controllers.ProfesorModelAssembler;
import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.exceptions.InvalidFieldException;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.exceptions.SearchParamException;
import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final ProfesorModelAssembler assembler;

    public ProfesorService(ProfesorRepository profesorRepository, ProfesorModelAssembler assembler) {
        this.profesorRepository = profesorRepository;
        this.assembler = assembler;
    }

    public Page<Profesor> ProfesorSearch(Pageable pageable,
                                         String nume, String prenume, String email, Grad grad, Asociere asociere, boolean fromArhive) {

        if (nume != null && nume.length() > 50) {
            throw new SearchParamException(nume);
        }
        if (prenume != null && prenume.length() > 50) {
            throw new SearchParamException(prenume);
        }
        EmailValidator emailValidator = new EmailValidator();
        if (email != null && (email.length() > 50 || !emailValidator.isValid(email, null))) {
            throw new SearchParamException(email);
        }

        return profesorRepository.findAll(Specification.where(
                nameContains(nume)
                        .and(prenumeContains(prenume))
                        .and(emailContains(email))
                        .and(gradEquals(grad))
                        .and(asociareEquals(asociere))
                        .and(arhivareEquals(fromArhive))
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

    // pentru selectarea profesorilor arhivati
    public static Specification<Profesor> arhivareEquals(boolean arhivat) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arhivat"), arhivat);
    }

    public EntityModel<ProfesorDto> AddProfesor(ProfesorDto profesorDto) {

        String nume = profesorDto.getNume();
        String prenume = profesorDto.getPrenume();
        String email = profesorDto.getEmail();
        String afiliere = profesorDto.getAfiliere();

        if (nume.length() > 50 || nume.length() < 2) {
            throw new InvalidFieldException("Numele este invalid");
        }
        if (prenume.length() > 50 || prenume.length() < 2) {
            throw new InvalidFieldException("Prenumele este invalid");
        }
        EmailValidator emailValidator = new EmailValidator();
        if (email.length() > 50 || !emailValidator.isValid(email, null)
                || profesorRepository.existsProfesorByEmail(email)) {
            throw new InvalidFieldException("Emailul este invalid");
        }
        if (afiliere.length() > 100) {
            throw new InvalidFieldException("Afilierea este invalidă");
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
                    if (nume.length() > 50 || nume.length() < 2) {
                        throw new InvalidFieldException("Numele este invalid");
                    }
                    profesor.setNume(nume);
                }
                case "prenume" -> {
                    String prenume = value;
                    if (prenume.length() > 50 || prenume.length() < 2) {
                        throw new InvalidFieldException("Prenumele este invalid");
                    }
                    profesor.setPrenume(prenume);
                }
                case "email" -> {
                    String email = value;
                    EmailValidator emailValidator = new EmailValidator();
                    if (email.length() > 50 || !emailValidator.isValid(email, null)
                            || profesorRepository.existsProfesorByEmail(email)) {
                        throw new InvalidFieldException("Emailul este invalid");
                    }
                    profesor.setEmail(email);
                }
                case "gradDidactic" -> {
                    String gradValue = value;
                    try {
                        Grad gradDidactic = Grad.valueOf(gradValue.toUpperCase());
                        profesor.setGradDidactic(gradDidactic);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("GradDidactic este invalid");
                    }
                }
                case "tipAsociere" -> {
                    String asociereValue = value;
                    try {
                        Asociere tipAsociere = Asociere.valueOf(asociereValue.toUpperCase());
                        profesor.setTipAsociere(tipAsociere);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFieldException("TipAsociere este invalid");
                    }
                }
                case "afiliere" -> {
                    String afiliere = value;
                    if (afiliere.length() > 100) {
                        throw new InvalidFieldException("Afilierea este invalidă");
                    }
                    profesor.setAfiliere(afiliere);
                }
                default -> throw new InvalidFieldException("Câmpul " + fieldName + " nu este valid");
            }
        });

        profesorRepository.save(profesor);

        return profesor;
    }
}
