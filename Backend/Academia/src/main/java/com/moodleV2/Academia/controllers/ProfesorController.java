package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import com.moodleV2.Academia.service.ProfesorService;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/academia")
//@Validated
public class ProfesorController {

    private final ProfesorRepository repository;
    private final ProfesorModelAssembler assembler;
    private final ProfesorService service;

    public ProfesorController(ProfesorRepository repository, ProfesorModelAssembler assembler, ValidationAutoConfiguration validationAutoConfiguration, ProfesorService service) {
        this.repository = repository;
        this.assembler = assembler;
        this.service = service;
    }

    @GetMapping("/profesori")
    @Operation(summary = "Get all profesori with pagination",
            description = "Retrieve a paginated list of profesori resources.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "416", description = "Pagination parameters not in range"),
            @ApiResponse(responseCode = "422", description = "Sorting parameters not valid")
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: nume, prenume, email, gradDidactic, tipAsociere, asc, desc", example = "nume,asc")
    ResponseEntity<?> getAll(Pageable pageable,
                             @RequestParam(name = "nume", required = false) String nume,
                             @RequestParam(name = "prenume", required = false) String prenume,
                             @RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "grad", required = false) Grad grad,
                             @RequestParam(name = "asociare", required = false) Asociere asociere) {    // DONE: verificare parametri de paginare

        // verificare dimeniuni paginare
        // TODO: add PROFESOR_MAX_COUNT and PROFESOR_MAX_PAGE_SIZE constants in application properties
        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        // PropertyReferenceException is thrown when sorting cannot be done by the specified parameter
        // 422 Unprocessable Content?
        Page<Profesor> page = service.ProfesorSearch(pageable, nume, prenume, email, grad, asociere);

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
                linkTo(methodOn(ProfesorController.class).getAll(pageable, null, null, null, null, null)).withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, null, null, null, null, null)).toUriComponentsBuilder()
                    .queryParam("page",pageable.getPageNumber() - 1)
                    .queryParam("size", pageable.getPageSize())
                    .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, null, null, null, null, null)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, null, null, null, null, null)).toUriComponentsBuilder()
                                .queryParam("page",0)
                                .queryParam("size", pageable.getPageSize())
                                .toUriString())
                        .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, null, null, null, null, null)).toUriComponentsBuilder()
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
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "404", description = "Profesor not found")
    })
    ResponseEntity<?> getById(@PathVariable Long id) {

//        if (id > 1000) {
//            throw new
//        }

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
