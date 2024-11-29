package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.exceptions.DisciplinaNotFoundException;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: nume, prenume, email, gradDidactic, tipAsociere, asc, desc", example = "nume,asc")
    ResponseEntity<?> getAll(Pageable pageable) {

        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        List<EntityModel<DisciplinaDto>> discipline = repository.findAll()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(discipline);
    }

    @GetMapping("/discipline/{code}")
    ResponseEntity<?> getByCode(@PathVariable String code) {

        Disciplina disciplina = repository.findById(code)
                .orElseThrow(() -> new DisciplinaNotFoundException(code));


        return ResponseEntity.ok(assembler.toModel(disciplina));
    }
}
