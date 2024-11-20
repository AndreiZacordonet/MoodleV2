package com.moodleV2.Academia.service;

import com.moodleV2.Academia.exceptions.SearchParamException;
import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final EmailValidator emailValidator;

    public ProfesorService(ProfesorRepository profesorRepository, EmailValidator emailValidator) {
        this.profesorRepository = profesorRepository;
        this.emailValidator = emailValidator;
    }

    public Page<Profesor> ProfesorSearch(Pageable pageable,
                                         String nume, String prenume, String email, Grad grad, Asociere asociere) {

        if (nume != null && nume.length() > 50) {
            throw new SearchParamException(nume);
        }
        if (prenume != null && prenume.length() > 50) {
            throw new SearchParamException(prenume);
        }
        if (email != null && (email.length() > 50 || !emailValidator.isValid(email, null))) {
            throw new SearchParamException(email);
        }

        return profesorRepository.findAll(Specification.where(
                nameContains(nume)
                        .and(prenumeContains(prenume))
                        .and(emailContains(email))
                        .and(gradEquals(grad))
                        .and(asociareEquals(asociere))
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
}
