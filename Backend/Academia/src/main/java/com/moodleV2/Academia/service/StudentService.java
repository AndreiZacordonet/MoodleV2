package com.moodleV2.Academia.service;

import com.moodleV2.Academia.exceptions.SearchParamException;
import com.moodleV2.Academia.models.Ciclu;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.models.Student;
import com.moodleV2.Academia.repositories.StudentRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> studentSearch(Pageable pageable, String nume, String prenume, String email, Ciclu ciclu, Integer an, Integer grupa, boolean arhivat) {

        // FIXME: data validations
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
        if (an != null && (an < 1 || an > 4)) {
            throw new SearchParamException(an);
        }
        if (grupa != null && (grupa < 1 || grupa > 50)) {
            throw new SearchParamException(grupa);
        }

        return studentRepository.findAll(Specification.where(
                nameContains(nume)
                        .and(prenumeContains(prenume))
                        .and(emailContains(email))
                        .and(cicluEquals(ciclu))
                        .and(anEquals(an))
                        .and(grupaEquals(grupa))
                        .and(arhivareEquals(arhivat))
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
}
