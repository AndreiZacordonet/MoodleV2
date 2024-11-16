package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
@RequestMapping("/api/academia")
public class ProfesorController {

    private final ProfesorRepository repository;
    private final ProfesorModelAssembler assembler;

    public ProfesorController(ProfesorRepository repository, ProfesorModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/profesori")
    public CollectionModel<EntityModel<ProfesorDto>> getAll() {
        List<EntityModel<ProfesorDto>> profesori =
                repository.findAll().stream()
                        .map(assembler::toModel)
                        .toList();

        return CollectionModel.of(profesori,
                linkTo(methodOn(ProfesorController.class).getAll()).withSelfRel());
    }

    @GetMapping("/profesori/{id}")
    public EntityModel<ProfesorDto> getById(@PathVariable Long id) {
        Profesor profesor = repository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));
//        ProfesorDto profesorDto = assembler.toModel(profesor).getContent();
//        System.out.println(profesorDto.toString());
        return assembler.toModel(profesor);
    }

    @Operation(summary = "Create a new Profesor", description = "Creates a new profesor using the provided DTO")
    @PostMapping("/profesori")
    ResponseEntity<?> createNew(@Valid @RequestBody ProfesorDto newProfesor) {
        EntityModel<ProfesorDto> profesorEntityModel = assembler.toModel(repository.save(newProfesor.ProfesorMapper()));

        return ResponseEntity.created(profesorEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(profesorEntityModel);
    }

}
