package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.dto.StudentDto;
import com.moodleV2.Academia.exceptions.InvalidFieldException;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.exceptions.ProfesorArchivedException;
import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import com.moodleV2.Academia.models.Asociere;
import com.moodleV2.Academia.models.Grad;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.repositories.ProfesorRepository;
import com.moodleV2.Academia.service.ProfesorService;
import com.moodleV2.Academia.service.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static com.moodleV2.Academia.service.Utils.createPagedModel;
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

    /**
     * Retrieves a paginated list of professors with optional filtering and sorting criteria.
     *
     * @param pageable Pagination details, including page number, size, and sorting criteria.<br>
     *                 - page: Page number, starting from 0 (default 0).<br>
     *                 - size: Number of items per page (default 20).<br>
     *                 - sort: Sorting criteria, e.g., "nume,asc".
     * @param nume Filter by professor's last name. Partial matches are allowed, and the search is case-insensitive.
     * @param prenume Filter by professor's first name. Partial matches are allowed, and the search is case-insensitive.
     * @param email Filter by professor's email address. Must be a valid email format.
     * @param grad Filter by professor's academic rank. Possible values:<br>
     *             - ASISTENT, SEF_LUCRARI, CONFERENTIAR, PROFESOR.
     * @param asociere Filter by professor's association type. Possible values:<br>
     *                 - TITULAR, ASOCIAT, EXTERN.
     * @param codDisciplina Filter by the unique code of the class taught by the professor.
     * @param numeDisciplina Filter by the name of the class taught by the professor.
     * @return A paginated response containing a list of professors as {@code ProfesorDto} objects, along with pagination metadata
     *         and navigation links for previous, next, first, and last pages.
     */
    @GetMapping("/profesori")
    @Operation(summary = "Retrieve all professors information.",
            description = "Specific filtering can be applied, the result is shown in a page based on selected preferences.")
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
                             @RequestParam(name = "numeDisciplina", required = false) String numeDisciplina
    ) {

        // TODO: add PROFESOR_MAX_COUNT and PROFESOR_MAX_PAGE_SIZE constants in application properties
        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        // PropertyReferenceException is thrown when sorting cannot be done by the specified parameter
        // 422 Unprocessable Content
        Page<Profesor> page = service.ProfesorSearch(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina, false);

        List<EntityModel<ProfesorDto>> profesori =
                page.getContent().stream()
                        .map(assembler::toModel)
                        .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/academia/profesori");

        PagedModel<EntityModel<ProfesorDto>> pagedModel = createPagedModel(
                profesori,
                metadata,
                uriBuilder,
                page.getNumber(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(pagedModel);
    }


    /**
     * Retrieves full details of a specific professor based on their unique identifier.
     *
     * @param id The unique identifier of the professor to be retrieved. Must be a positive integer.
     * @return A {@code ProfesorDto} object containing detailed information about the professor,
     *         including their name, email, academic rank, and associated disciplines.
     * @throws ProfesorNotFoundException if no professor is found with the provided identifier.
     * @throws IndexOutOfBoundsException if the identifier is outside the valid range.
     */
    @GetMapping("/profesori/{id}")
    @Operation(summary = "Retrieve ONE professor full informations.",
            description = "Searches by the provided professor-code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor unique code", example = "2")
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


    /**
     * Creates a new professor in the system.
     *
     * @param newProfesor A {@code ProfesorDto} object containing the details of the professor to be created.<br>
     *                    Must include valid values for name, email, academic rank, and association type.
     * @return The created {@code ProfesorDto} object wrapped in an {@code EntityModel}, along with a {@code 201 Created} status
     *         and a link to the newly created resource.
     * @throws InvalidFieldException if the provided data is incomplete or invalid.
     */
    @PostMapping("/profesori")
    @Operation(summary = "Create a new professor.",
            description = "Using the sent data, checks for its correctness and proceeds to create and store the new professor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professor created successfully", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "406", description = "Parameter format is not correct"),
            @ApiResponse(responseCode = "422", description = "Professor data is invalid")
    })
    ResponseEntity<?> createNew(@Validated @RequestBody ProfesorDto newProfesor) {

        EntityModel<ProfesorDto> profesorEntityModel = service.AddProfesor(newProfesor);

        return ResponseEntity.created(profesorEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(profesorEntityModel);
    }


    /**
     * Archives a professor by their unique identifier, marking them as inactive.
     *
     * @param id The unique identifier of the professor to be archived. Must be a positive integer.
     * @return A confirmation of the archival operation, including the updated professor details as a {@code ProfesorDto}.
     * @throws ProfesorNotFoundException if no professor is found with the provided identifier.
     * @throws ProfesorArchivedException if the professor is already archived.
     * @throws IndexOutOfBoundsException if the identifier is outside the valid range.
     */
    @DeleteMapping("/profesori/{id}")
    @Operation(summary = "Archives a specified professor.",
            description = "Using the provided code, it will switch the archive flag thus marking it as being archived.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully archived"),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor unique code", example = "2")
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


    /**
     * Partially updates the details of a specific professor.
     *
     * @param id The unique identifier of the professor to be updated. Must be a positive integer.
     * @param fields A map of fields to be updated. Only the provided fields will be modified.<br>
     *               Example of valid fields:<br>
     *               - "nume": Updated last name.<br>
     *               - "prenume": Updated first name.<br>
     *               - "email": Updated email address.<br>
     *               - "gradDidactic": Updated academic rank.<br>
     *               - "tipAsociere": Updated association type.<br>
     *               - "afiliere": Updated affiliation.
     * @return The updated {@code ProfesorDto} object as an {@code EntityModel}.
     * @throws ProfesorNotFoundException if no professor is found with the provided identifier.
     * @throws InvalidFieldException if the provided fields are invalid.
     */
    @PatchMapping("/profesori/{id}")
    @Operation(summary = "Updates specified professor data.",
            description = "Provided fields, if syntactically and logically correct, will be updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = ProfesorDto.class))),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier"),
            @ApiResponse(responseCode = "422", description = "Professor data is invalid")
    })
    @Parameter(name = "id", description = "Professor unique code", example = "2")
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


    /**
     * Removes a professor from the archived state, marking them as active.
     *
     * @param id The unique identifier of the professor to be unarchived. Must be a positive integer.
     * @return A confirmation of the unarchival operation, including the updated professor details as a {@code ProfesorDto}.
     * @throws ProfesorNotFoundException if no professor is found with the provided identifier.
     * @throws ProfesorArchivedException if the professor is not currently archived.
     * @throws IndexOutOfBoundsException if the identifier is outside the valid range.
     */
    @PostMapping("/profesori/{id}/activate")
    @Operation(summary = "Removes specified professor from archive.",
            description = "If the provided code is correct, marks the course as being active by switching the archive flag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor unique code", example = "2")
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
     * Retrieves a paginated list of archived professors with optional filtering and sorting criteria.
     *
     * @param pageable Pagination details, including page number, size, and sorting criteria.<br>
     *                 - page: Page number, starting from 0 (default 0).<br>
     *                 - size: Number of items per page (default 20).<br>
     *                 - sort: Sorting criteria, e.g., "nume,asc".
     * @param nume Filter by professor's last name. Partial matches are allowed, and the search is case-insensitive.
     * @param prenume Filter by professor's first name. Partial matches are allowed, and the search is case-insensitive.
     * @param email Filter by professor's email address. Must be a valid email format.
     * @param grad Filter by professor's academic rank. Possible values:<br>
     *             - ASISTENT, SEF_LUCRARI, CONFERENTIAR, PROFESOR.
     * @param asociere Filter by professor's association type. Possible values:<br>
     *                 - TITULAR, ASOCIAT, EXTERN.
     * @param codDisciplina Filter by the unique code of the class taught by the professor.
     * @param numeDisciplina Filter by the name of the class taught by the professor.
     * @return A paginated response containing a list of archived professors as {@code ProfesorDto} objects, along with pagination
     *         metadata and navigation links for previous, next, first, and last pages.
     */
    @GetMapping("/profesori/archive")
    @Operation(summary = "Retrieve all ARCHIVED professors information.",
            description = "Specific filtering can be applied, the result is shown in a page based on selected preferences.")
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
                                  @RequestParam(name = "numeDisciplina", required = false) String numeDisciplina
    ) {

        // TODO: add PROFESOR_MAX_COUNT and PROFESOR_MAX_PAGE_SIZE constants in application properties
        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        // PropertyReferenceException is thrown when sorting cannot be done by the specified parameter
        // 422 Unprocessable Content
        Page<Profesor> page = service.ProfesorSearch(pageable, nume, prenume, email, grad, asociere, codDisciplina, numeDisciplina, true);

        List<EntityModel<ProfesorDto>> profesori =
                page.getContent().stream()
                        .map(assembler::toModel)
                        .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/academia/profesori/archive");

        PagedModel<EntityModel<ProfesorDto>> pagedModel = createPagedModel(
                profesori,
                metadata,
                uriBuilder,
                page.getNumber(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(pagedModel);

    }


    /**
     * Retrieves all disciplines (classes) associated with a specific professor.
     *
     * @param id The unique identifier of the professor whose disciplines are to be retrieved. Must be a positive integer.
     * @return A list of disciplines taught by the professor, represented as {@code DisciplinaDto} objects.
     * @throws ProfesorNotFoundException if no professor is found with the provided identifier.
     * @throws IndexOutOfBoundsException if the identifier is outside the valid range.
     */
    @GetMapping("/profesori/{id}/my-disciplines")
    @Operation(summary = "Retrieve a professors courses.",
            description = "Retrieve all courses specified by a professor identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor unique code", example = "2")
    ResponseEntity<?> getMyDisciplines(@PathVariable Long id) {

        List<EntityModel<DisciplinaDto>> disciplines = service.getMyDisciplines(id);

        CollectionModel<EntityModel<DisciplinaDto>> collectionModel = CollectionModel.of(disciplines);

        collectionModel.add(Link.of("/api/academia/discipline").withRel("discipline").withType("GET"));

        return ResponseEntity.ok(service.getMyDisciplines(id));
    }


    @GetMapping("/profesori/{id}/all-disciplines")
    @Operation(summary = "Retrieve all courses.",
            description = "Retrieve all courses specified by a professor identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Professor unique code", example = "2")
    ResponseEntity<?> getAllDisciplines(@PathVariable Long id) {

        List<EntityModel<DisciplinaDto>> disciplines = service.getAllDisciplines(id);

        CollectionModel<EntityModel<DisciplinaDto>> collectionModel = CollectionModel.of(disciplines);

        collectionModel.add(Link.of("/api/academia/discipline").withRel("discipline").withType("GET"));

        return ResponseEntity.ok(collectionModel);
    }


    // TODO: get all students?
    @GetMapping("/profesori/{id}/studenti")
    ResponseEntity<?> getStudenti(@PathVariable Long id) {

        List<EntityModel<StudentDto>> students = service.getMyStudents(id);

        CollectionModel<EntityModel<StudentDto>> collectionModel = CollectionModel.of(students);

        collectionModel.add(Link.of("/api/academia/studenti").withRel("studenti").withType("GET"));

        return ResponseEntity.ok(collectionModel);
    }

    // TODO: get all student by course?
    // or maybe not
}
