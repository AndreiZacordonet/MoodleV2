package com.moodleV2.Academia.repositories;

import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfesorRepository extends JpaRepository<Profesor, Long>, JpaSpecificationExecutor<Profesor> {
    boolean existsProfesorByEmail(String email);

    Profesor getDistinctById(Long idTitular);
}
