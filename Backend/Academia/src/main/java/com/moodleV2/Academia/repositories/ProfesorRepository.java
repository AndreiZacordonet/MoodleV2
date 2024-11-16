package com.moodleV2.Academia.repositories;

import com.moodleV2.Academia.models.Profesor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Page<Profesor> findAll(Pageable pageable);
}
