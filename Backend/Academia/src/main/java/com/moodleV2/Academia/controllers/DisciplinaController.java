package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academia")
public class DisciplinaController {

    private final DisciplinaRepository repository;
    private final DisciplinaModelAssembler assembler;

    public DisciplinaController(DisciplinaRepository repository, DisciplinaModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/discipline")
    ResponseEntity<?> getAll() {

        List<EntityModel<DisciplinaDto>> discipline = repository.findAll()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(discipline);
    }
}
