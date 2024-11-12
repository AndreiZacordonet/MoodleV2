package com.moodleV2.Academia.repositories;

import com.moodleV2.Academia.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
