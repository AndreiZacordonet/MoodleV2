package com.moodleV2.Academia.repositories;

import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsStudentByEmail(String email);

    List<Student> searchStudentsByClasses(Disciplina disciplina);

    Set<Student> getStudentsByClasses(Disciplina d);
}
