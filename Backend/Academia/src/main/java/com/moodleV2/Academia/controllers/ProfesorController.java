package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/academia")
public class ProfesorController {

    private final ProfesorRepository repository;
    private final ProfesorModelAssembler assembler;

    public ProfesorController(ProfesorRepository repository, ProfesorModelAssembler assembler, ValidationAutoConfiguration validationAutoConfiguration) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/profesori")
    @Operation(summary = "Get all profesori with pagination",
            description = "Retrieve a paginated list of profesori resources.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria, e.g., nume,asc", example = "nume,asc")
    ResponseEntity<?> getAll(@Parameter Pageable pageable) {
        Page<Profesor> page = repository.findAll(pageable);

        List<EntityModel<ProfesorDto>> profesori =
                page.getContent().stream()
                        .map(assembler::toModel)
                        .toList();

        PagedModel<EntityModel<ProfesorDto>> pagedModel = PagedModel.of(
                profesori,
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                linkTo(methodOn(ProfesorController.class).getAll(pageable)).withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable)).toUriComponentsBuilder()
                    .queryParam("page",pageable.getPageNumber() - 1)
                    .queryParam("size", pageable.getPageSize())
                    .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable)).toUriComponentsBuilder()
                                .queryParam("page",0)
                                .queryParam("size", pageable.getPageSize())
                                .toUriString())
                        .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable)).toUriComponentsBuilder()
                        .queryParam("page",page.getTotalPages() - 1)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("last")
        );

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/profesori/{id}")
    @Operation(summary = "Retrive one professor", description = "Retrive one professor by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrievde", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "404", description = "Profesor not found")
    })
    ResponseEntity<?> getById(@PathVariable Long id) {

        Profesor profesor = repository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        return ResponseEntity.ok(assembler.toModel(profesor));
    }

    @PostMapping("/profesori")
    @Operation(summary = "Create a new Profesor", description = "Creates a new profesor using the provided DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professor created successfully", content = @Content(schema = @Schema(implementation = ProfesorDto.class)))
    })
    ResponseEntity<?> createNew(@Valid @RequestBody ProfesorDto newProfesor) {

        EntityModel<ProfesorDto> profesorEntityModel = assembler.toModel(repository.save(newProfesor.ProfesorMapper()));

        return ResponseEntity.created(profesorEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(profesorEntityModel);
    }

}
