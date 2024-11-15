package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProfesorController {

    private final ProfesorRepository repository;
    private final ProfesorModelAssembler assembler;

    public ProfesorController(ProfesorRepository repository, ProfesorModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/api/academia/profesori")
    public CollectionModel<EntityModel<Profesor>> getAll() {
        List<EntityModel<Profesor>> profesori =
                repository.findAll().stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toUnmodifiableList());

        return CollectionModel.of(profesori,
                linkTo(methodOn(ProfesorController.class).getAll()).withSelfRel());
    }

    @GetMapping("/api/academia/profesori/{id}")
    public EntityModel<Profesor> getById(@PathVariable Long id) {
        Profesor profesor = repository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        return assembler.toModel(profesor);
    }

    @PostMapping("/api/academia/profesori")
    ResponseEntity<?> createNew(@RequestBody Profesor newProfesor) {
        EntityModel<Profesor> profesorEntityModel = assembler.toModel(repository.save(newProfesor));

        return ResponseEntity.created(profesorEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(profesorEntityModel);
    }

}
