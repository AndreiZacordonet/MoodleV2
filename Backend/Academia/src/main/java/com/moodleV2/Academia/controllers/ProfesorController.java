package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.exceptions.ProfesorArchivedException;
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
import org.springframework.data.domain.Page;
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
    private final ProfesorService service;

    public ProfesorController(ProfesorRepository repository, ProfesorModelAssembler assembler, ProfesorService service) {
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
    @Parameter(name = "nume", description = "Filter by professor's last name (partial match allowed). Case-insensitive.", example = "Popescu")
    @Parameter(name = "prenume", description = "Filter by professor's first name (partial match allowed). Case-insensitive.", example = "Ion")
    @Parameter(name = "email", description = "Filter by professor's email address. Must be a valid email format.", example = "ion.popescu@example.com")
    @Parameter(name = "grad", description = "Filter by professor's academic rank. Allowed values: ASISTENT, SEF_LUCRARI, CONFERENTIAR, PROFESOR", schema = @Schema(implementation = Grad.class))
    @Parameter(name = "asociare", description = "Filter by professor's type of association. Allowed values: TITULAR, ASOCIAT, EXTERN", schema = @Schema(implementation = Asociere.class))
    @Parameter(name = "codDisciplina", description = "Filter by class code", example = "MATH69")
    @Parameter(name = "numeDisciplina", description = "Filter by class name", example = "Matematici interesante si dragute")
    ResponseEntity<?> getAll(Pageable pageable,
                             @RequestParam(name = "nume", required = false) String nume,
                             @RequestParam(name = "prenume", required = false) String prenume,
                             @RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "grad", required = false) Grad grad,
                             @RequestParam(name = "asociare", required = false) Asociere asociere,
                             @RequestParam(name = "codDisciplina", required = false) String codDisciplina,
                             @RequestParam(name = "numeDisciplina", required = false) String numeDisciplina) {    // DONE: verificare parametri de paginare

        // check pagination dimentions
        // TODO: add PROFESOR_MAX_COUNT and PROFESOR_MAX_PAGE_SIZE constants in application properties
        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        // PropertyReferenceException is thrown when sorting cannot be done by the specified parameter
        // 422 Unprocessable Content?
        Page<Profesor> page = service.ProfesorSearch(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina, false);

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
                Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                                .queryParam("page", page.getNumber())
                                .queryParam("size", page.getSize())
                                .toUriString())
                        .withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                    .queryParam("page",pageable.getPageNumber() - 1)
                    .queryParam("size", pageable.getPageSize())
                    .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                                .queryParam("page",0)
                                .queryParam("size", pageable.getPageSize())
                                .toUriString())
                        .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getAll(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
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
            @ApiResponse(responseCode = "404", description = "Profesor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    ResponseEntity<?> getById(@PathVariable Long id) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Profesor profesor = repository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        // if professor is archived then don't return a professor
        if (profesor.isArhivat()) {
            throw new ProfesorNotFoundException(id);
        }

        return ResponseEntity.ok(assembler.toModel(profesor));
    }

    @PostMapping("/profesori")
    @Operation(summary = "Create a new Profesor", description = "Creates a new profesor using the provided DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professor created successfully", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "422", description = "Profesor data is invalid")
    })
    ResponseEntity<?> createNew(@Valid @RequestBody ProfesorDto newProfesor) {
        // DONE: profesor data validation
        // DONE: check if profesor already exists (check email)

        EntityModel<ProfesorDto> profesorEntityModel = service.AddProfesor(newProfesor);

        return ResponseEntity.created(profesorEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(profesorEntityModel);
    }



    /**
     * Archives a teacher by its Id. <br/>
     * If the teacher is already archived throws an error <br/>
     * This endpoint is only accessible to admins.
     * @param id
     * @return {@code Profesor DTO} incapsulated in a {@code ResponseEntity}
     */
    @DeleteMapping("/profesori/{id}")
    @Operation(summary = "Archives one professor", description = "Archives one professor by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully archived"),
            @ApiResponse(responseCode = "404", description = "Profesor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor ID to be archived", example = "123")
    ResponseEntity<?> archiveById(@PathVariable Long id) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Profesor profesor = repository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        // if professor is archived then don't return a professor
        if (profesor.isArhivat()) {
            throw new ProfesorArchivedException("Professor with id " + id + " is already archived");
        }

        profesor.setArhivat(true);
        repository.save(profesor);

        return ResponseEntity.ok(assembler.toModel(profesor));
    }


    @PatchMapping("/profesori/update/{id}")
    @Operation(summary = "Update a professor", description = "Updates one or more fields of a professor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "404", description = "Profesor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier"),
            @ApiResponse(responseCode = "422", description = "Profesor data is invalid")
    })
    @Parameter(name = "id", description = "Professor ID to be updated", example = "123")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to be updated. Only the provided fields will be modified.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                          "nume": "Updated Nume",
                          "prenume": "Updated Prenume",
                          "email": "updated.email@example.com",
                          "gradDidactic": "PROFESOR",
                          "tipAsociere": "TITULAR",
                          "afiliere": "Updated Afiliere"
                        }
                        """)
            ))
    ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody Map<String, String> fields) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Profesor profesor = service.PartialUpdateProfesor(id, fields);

        return ResponseEntity.ok(assembler.toModel(profesor));
    }


    // DONE: add getArchivedProfesori endpoint
    // DONE: add un-archived Profesor
    /**
     * Un-Archives a teacher by its Id. <br/>
     * Does the opposite as {@code archiveById} <br/>
     * If the teacher is not archived throws an error <br/>
     * This endpoint is only accessible to admins.
     * @param id
     * @return {@code Profesor DTO} incapsulated in a {@code ResponseEntity}
     */
    @PostMapping("/profesori/{id}/activate")
    @Operation(summary = "Un-Archives one professor", description = "Un-Archives one professor by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "404", description = "Profesor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor ID to be un-archived", example = "123")
    ResponseEntity<?> removeFromArchiveById(@PathVariable Long id) {

        if (id > 1000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Profesor profesor = repository.findById(id)
                .orElseThrow(() -> new ProfesorNotFoundException(id));

        // if professor is archived then don't return a professor
        if (!profesor.isArhivat()) {
            throw new ProfesorArchivedException("Professor with id " + id + " is NOT archived");
        }

        profesor.setArhivat(false);
        repository.save(profesor);

        return ResponseEntity.ok(assembler.toModel(profesor));
    }


    /**
     * Retrives all archived teachers <br>
     * The endpoint is only accessible to admins.
     * @param pageable
     * @param nume
     * @param prenume
     * @param email
     * @param grad
     * @param asociere
     * @param codDisciplina
     * @param numeDisciplina
     * @return {@code Profesor DTO list} incapsulated in a {@code ResponseEntity}
     */
    @GetMapping("/profesori/archive/")
    @Operation(summary = "Retrives all archived professors", description = "Retrives all archived professors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "416", description = "Pagination parameters not in range"),
            @ApiResponse(responseCode = "422", description = "Sorting parameters not valid")
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: nume, prenume, email, gradDidactic, tipAsociere, asc, desc", example = "nume,asc")
    @Parameter(name = "nume", description = "Filter by professor's last name (partial match allowed). Case-insensitive.", example = "Popescu")
    @Parameter(name = "prenume", description = "Filter by professor's first name (partial match allowed). Case-insensitive.", example = "Ion")
    @Parameter(name = "email", description = "Filter by professor's email address. Must be a valid email format.", example = "ion.popescu@example.com")
    @Parameter(name = "grad", description = "Filter by professor's academic rank. Allowed values: ASISTENT, SEF_LUCRARI, CONFERENTIAR, PROFESOR", schema = @Schema(implementation = Grad.class))
    @Parameter(name = "asociare", description = "Filter by professor's type of association. Allowed values: TITULAR, ASOCIAT, EXTERN", schema = @Schema(implementation = Asociere.class))
    @Parameter(name = "codDisciplina", description = "Filter by class code", example = "MATH69")
    @Parameter(name = "numeDisciplina", description = "Filter by class name", example = "Matematici interesante si dragute")
    ResponseEntity<?> getArchived(Pageable pageable,
                                  @RequestParam(name = "nume", required = false) String nume,
                                  @RequestParam(name = "prenume", required = false) String prenume,
                                  @RequestParam(name = "email", required = false) String email,
                                  @RequestParam(name = "grad", required = false) Grad grad,
                                  @RequestParam(name = "asociare", required = false) Asociere asociere,
                                  @RequestParam(name = "codDisciplina", required = false) String codDisciplina,
                                  @RequestParam(name = "numeDisciplina", required = false) String numeDisciplina) {    // DONE: verificare parametri de paginare

        // check page dimentions
        // TODO: add PROFESOR_MAX_COUNT and PROFESOR_MAX_PAGE_SIZE constants in application properties
        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        // PropertyReferenceException is thrown when sorting cannot be done by the specified parameter
        // 422 Unprocessable Content?
        Page<Profesor> page = service.ProfesorSearch(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina, true);

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
                Link.of(linkTo(methodOn(ProfesorController.class).getArchived(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                                .queryParam("page", page.getNumber())
                                .queryParam("size", page.getSize())
                                .toUriString())
                        .withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getArchived(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() - 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getArchived(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getArchived(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                        .queryParam("page",0)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(ProfesorController.class).getArchived(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina)).toUriComponentsBuilder()
                        .queryParam("page",page.getTotalPages() - 1)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("last")
        );

        return ResponseEntity.ok(pagedModel);

    }
}
