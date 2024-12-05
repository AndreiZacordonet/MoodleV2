package com.moodleV2.Academia.repositories;

import com.moodleV2.Academia.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsStudentByEmail(String email);
}
