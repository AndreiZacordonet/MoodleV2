package com.moodleV2.Academia.repositories;

import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, String> {
    List<Disciplina> findByIdTitular(Profesor prof);
}
