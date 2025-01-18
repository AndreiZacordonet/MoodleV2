package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.dto.DisciplinaDtoCreateNew;
import com.moodleV2.Academia.exceptions.DisciplinaArchivedException;
import com.moodleV2.Academia.exceptions.DisciplinaNotFoundException;
import com.moodleV2.Academia.exceptions.InvalidFieldException;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.models.Categorie;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.TipDisciplina;
import com.moodleV2.Academia.models.TipExaminare;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import com.moodleV2.Academia.service.DisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/academia")
public class DisciplinaController {

    private final DisciplinaRepository repository;
    private final DisciplinaService service;
    private final DisciplinaModelAssembler assembler;

    public DisciplinaController(DisciplinaRepository repository, DisciplinaService service, DisciplinaModelAssembler assembler) {
        this.repository = repository;
        this.service = service;
        this.assembler = assembler;
    }


    /**
     * Retrieves all course information with optional filtering and pagination.
     *
     * @param pageable          Defines the pagination and sorting information.<br>
     *                          - page: Page number, starting at 0.<br>
     *                          - size: Number of items per page.<br>
     *                          - sort: Sorting criteria (e.g., "numeDisciplina,asc").
     * @param cod               (Optional) Course unique code for filtering (e.g., "MATH69").
     * @param nume              (Optional) Course name for filtering (e.g., "Matematici banale").
     * @param anStudiu          (Optional) Study year of the course for filtering (e.g., 4).
     * @param tipDisciplina      (Optional) Type of course for filtering (e.g., "IMPUSA").
     * @param categorie          (Optional) Course category for filtering (e.g., "DOMENIU").
     * @param tipExaminare       (Optional) Type of examination for filtering (e.g., "EXAMEN").
     * @param numePrenumeTitular (Optional) Professor's full name for filtering (e.g., "Popesco Cristi").
     * @param emailTitular       (Optional) Professor's email address for filtering (e.g., "andrei74c0@gmail.com").
     * @return {@code ResponseEntity} containing a paginated and filtered list of course information.<br>
     *         Links to first, last, previous, and next pages are included in the response.
     */
    @GetMapping("/discipline")
    @Operation(summary = "Retrieve all courses information.",
            description = "Specific filtering can be applied, the result is shown in a page based on selected preferences.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "416", description = "Pagination parameters not in range"),
            @ApiResponse(responseCode = "422", description = "Sorting parameters not valid")
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: numeDisciplina, cod, anStudiu, categorie, tipExaminare, tipDisciplina, asc, desc", example = "numeDisciplina,asc")
    @Parameter(name = "cod", description = "Course unique code", example = "MATH69")
    @Parameter(name = "numeDisciplina", description = "Course name", example = "Matematici banale")
    @Parameter(name = "anStudiu", description = "Study year that course belongs to", example = "4")
    @Parameter(name = "tipDisciplina", description = "Type of course", example = "IMPUSA")
    @Parameter(name = "categorie", description = "Course category", example = "DOMENIU")
    @Parameter(name = "tipExaminare", description = "Type of examination", example = "EXAMEN")
    @Parameter(name = "numePrenumeTitular", description = "Last & Firstname of professor", example = "Popesco Cristi")
    @Parameter(name = "emailTitular", description = "Email of professor", example = "andrei74c0@gmail.com")
    ResponseEntity<?> getAll(Pageable pageable,
                             @RequestParam(name = "cod", required = false) String cod,
                             @RequestParam(name = "numeDisciplina", required = false) String nume,
                             @RequestParam(name = "anStudiu", required = false) Integer anStudiu,
                             @RequestParam(name = "tipDisciplina",required = false) TipDisciplina tipDisciplina,
                             @RequestParam(name = "categorie", required = false) Categorie categorie,
                             @RequestParam(name = "tipExaminare", required = false) TipExaminare tipExaminare,
                             @RequestParam(name = "numePrenumeTitular", required = false) String numePrenumeTitular,
                             @RequestParam(name = "emailTitular", required = false) String emailTitular
    ) {

        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        Page<Disciplina> page = service.disciplinaSearch(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular, false);

        List<EntityModel<DisciplinaDto>> discipline = page.getContent().stream()
                .map(assembler::toModel)
                .toList();

        PagedModel<EntityModel<DisciplinaDto>> pagedModel = PagedModel.of(
                discipline,
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                        .queryParam("page", page.getNumber())
                        .queryParam("size", page.getSize())
                        .toUriString())
                        .withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() - 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                        .queryParam("page",0)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                        .queryParam("page",page.getTotalPages() - 1)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("last")
        );

        return ResponseEntity.ok(pagedModel);
    }


    /**
     * Retrieves the full information of a specific course by its unique code.
     *
     * @param code Unique code of the course to retrieve (e.g., "MATH69").
     * @return ResponseEntity containing detailed information of the course as a {@code DisciplinaDto} object.
     * @throws DisciplinaNotFoundException if the course is not found or archived.
     */
    @GetMapping("/discipline/{code}")
    @Operation(summary = "Retrieve ONE class full informations.",
            description = "Searches by the provided class-code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = DisciplinaDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "404", description = "Class not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "code", description = "Course unique code", example = "MATH69")
    ResponseEntity<?> getByCode(@PathVariable String code) {

        if (code.isEmpty() || code.length() > 20 || code.isBlank()) {
            throw new IndexOutOfBoundsException();
        }

        Disciplina disciplina = repository.findById(code)
                .orElseThrow(() -> new DisciplinaNotFoundException(code));

        if (disciplina.isArhivat()) {
            throw new DisciplinaNotFoundException(code);
        }

        return ResponseEntity.ok(assembler.toModel(disciplina));
    }


    /**
     * Creates a new course using the provided data.
     *
     * @param newDisciplina DisciplinaDtoCreateNew object containing the new course details:<br>
     *                      - nume: Course name.<br>
     *                      - cod: Unique course code.<br>
     *                      - anStudiu: Study year.<br>
     *                      - categorie: Course category.<br>
     *                      - tipDisciplina: Type of course.<br>
     *                      - tipExaminare: Type of examination.<br>
     *                      - titular: Information about the course professor.
     * @return {@code ResponseEntity} with the created course's details and a location link to the new resource.
     *         @throws InvalidFieldException if input data is invalid.
     */
    @PutMapping("/discipline")
    @Operation(summary = "Create a new course.",
            description = "Using the sent data, checks for its correctness and proceeds to create and store the new course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully", content = @Content(schema = @Schema(implementation = DisciplinaDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "409", description = "Course code already exists"),
            @ApiResponse(responseCode = "422", description = "Course data is invalid")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to be updated. Only the provided fields will be modified.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DisciplinaDtoCreateNew.class)
            ))
    ResponseEntity<?> createNew(@Valid @RequestBody DisciplinaDtoCreateNew newDisciplina,
                                HttpServletRequest request) {

        RestTemplate restTemplate = new RestTemplate();
        String materialsApiUrl = "http://localhost:8001/api/materials/";

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing");
        }

        // Build the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("code", newDisciplina.getCod());
        requestBody.put("evaluation", List.of(Map.of(
                "type", "FINAL_EXAM",
                "weight", 100
        )));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.exchange(
                materialsApiUrl,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to communicate with materials API");
        }

        EntityModel<DisciplinaDto> disciplinaDtoEntityModel = service.addDisciplina(newDisciplina);

        return ResponseEntity.created(disciplinaDtoEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(disciplinaDtoEntityModel);
    }


    /**
     * Archives a specified course using its unique code.
     *
     * @param code Unique code of the course to archive (e.g., "MATH69").
     * @return ResponseEntity containing the archived course's details as a {@code DisciplinaDto} object.
     * @throws DisciplinaNotFoundException if the course does not exist or is already archived.
     */
    @DeleteMapping("/discipline/{code}")
    @Operation(summary = "Archives a specified course.",
            description = "Using the provided code, it will switch the archive flag thus marking it as being archived.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully archived"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "code", description = "Course unique code", example = "MATH69")
    ResponseEntity<?> archiveById(@PathVariable String code) {

        if (code.isEmpty() || code.length() > 20 || code.isBlank()) {
            throw new IndexOutOfBoundsException();
        }

        Disciplina disciplina = repository.findById(code)
                .orElseThrow(() -> new DisciplinaNotFoundException(code));

        if (disciplina.isArhivat()) {
            throw new DisciplinaArchivedException("Class with code { " + code + " } is already archived");
        }

        disciplina.setArhivat(true);
        repository.save(disciplina);

        return ResponseEntity.ok(assembler.toModel(disciplina));
    }


    /**
     * Partially updates the specified fields of a course.
     *
     * @param code   Unique code of the course to update (e.g., "MATH69").
     * @param fields Map of fields to update. Example fields:<br>
     *               - nume: Updated course name.<br>
     *               - anStudiu: Updated study year.<br>
     *               - emailTitular: Updated professor's email.<br>
     *               - tipDisciplina: Updated type of course (e.g., "OPTIONALA").<br>
     *               - categorie: Updated course category (e.g., "ADIACENTA").<br>
     *               - tipExaminare: Updated type of examination (e.g., "EXAMEN").
     * @return {@code ResponseEntity} containing the updated course details as a {@code DisciplinaDto} object.
     *         Throws validation or not found exceptions if the input data or code is invalid.
     */
    @PatchMapping("/discipline/{code}")
    @Operation(summary = "Updates specified course data.",
            description = "Provided fields, if syntactically and logically correct, will be updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = DisciplinaDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier"),
            @ApiResponse(responseCode = "422", description = "Course data is invalid")
    })
    @Parameter(name = "code", description = "Course unique code", example = "MATH69")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to be updated. Only the provided fields will be modified.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                          "nume": "Updated Nume",
                          "anStudiu": "4",
                          "idTitular": "2",
                          "tipDisciplina": "OPTIONALA",
                          "categorie": "ADIACENTA",
                          "tipExaminare": "EXAMEN"
                        }
                        """)
            ))
    ResponseEntity<?> partialUpdate(@PathVariable String code, @RequestBody Map<String, String> fields) {

        if (code.isEmpty() || code.length() > 20 || code.isBlank()) {
            throw new IndexOutOfBoundsException();
        }

        Disciplina disciplina = service.partialUpdateDisciplina(code, fields);

        return ResponseEntity.ok(assembler.toModel(disciplina));
    }


    /**
     * Unarchives a specific course by its unique code.
     *
     * @param code Unique code of the course to unarchive (e.g., "MATH69").
     * @return {@code ResponseEntity} containing the unarchived course's details as a DisciplinaDto object.
     * @throws DisciplinaNotFoundException if the course does not exist or is not archived.
     */
    @PostMapping("/discipline/{code}/activate")
    @Operation(summary = "Removes specified course from archive.",
            description = "If the provided code is correct, marks the course as being active by switching the archive flag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "code", description = "Course unique code", example = "MATH69")
    ResponseEntity<?> removeFromArchiveByCode(@PathVariable String code) {

        if (code.isEmpty() || code.length() > 20) {
            throw new IndexOutOfBoundsException();
        }

        Disciplina disciplina = repository.findById(code)
                .orElseThrow(() -> new DisciplinaNotFoundException(code));

        if (!disciplina.isArhivat()) {
            throw new DisciplinaArchivedException("Class with code " + code + " is NOT archived");
        }

        disciplina.setArhivat(false);
        repository.save(disciplina);

        return ResponseEntity.ok(assembler.toModel(disciplina));
    }


    /**
     * Retrieves all archived course information with optional filtering and pagination.
     *
     * @param pageable          Defines the pagination and sorting information.<br>
     *                          - page: Page number, starting at 0.<br>
     *                          - size: Number of items per page.<br>
     *                          - sort: Sorting criteria (e.g., "numeDisciplina,asc").<br>
     * @param cod               (Optional) Archived course unique code for filtering (e.g., "MATH69").
     * @param nume              (Optional) Archived course name for filtering (e.g., "Matematici banale").
     * @param anStudiu          (Optional) Study year of the archived course for filtering (e.g., 4).
     * @param tipDisciplina      (Optional) Type of archived course for filtering (e.g., "IMPUSA").
     * @param categorie          (Optional) Archived course category for filtering (e.g., "DOMENIU").
     * @param tipExaminare       (Optional) Type of examination for archived course filtering (e.g., "EXAMEN").
     * @param numePrenumeTitular (Optional) Professor's full name for archived course filtering (e.g., "Popesco Cristi").
     * @param emailTitular       (Optional) Professor's email address for archived course filtering (e.g., "andrei74c0@gmail.com").
     * @return {@code ResponseEntity} containing a paginated and filtered list of archived course information.<br>
     *         Links to first, last, previous, and next pages are included in the response.
     */
    @GetMapping("/discipline/archive/")
    @Operation(summary = "Retrieve all ARCHIVED courses information.",
            description = "Specific filtering can be applied, the result is shown in a page based on selected preferences.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "403",description = "Access is forbidden"),
            @ApiResponse(responseCode = "416", description = "Pagination parameters not in range"),
            @ApiResponse(responseCode = "422", description = "Sorting parameters not valid")
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: numeDisciplina, cod, anStudiu, categorie, tipExaminare, tipDisciplina, asc, desc", example = "numeDisciplina,asc")
    @Parameter(name = "cod", description = "Course unique code", example = "MATH69")
    @Parameter(name = "numeDisciplina", description = "Course name", example = "Matematici banale")
    @Parameter(name = "anStudiu", description = "Study year that course belongs to", example = "4")
    @Parameter(name = "tipDisciplina", description = "Type of course", example = "IMPUSA")
    @Parameter(name = "categorie", description = "Course category", example = "DOMENIU")
    @Parameter(name = "tipExaminare", description = "Type of examination", example = "EXAMEN")
    @Parameter(name = "numePrenumeTitular", description = "Last & Firstname of professor", example = "Popesco Cristi")
    @Parameter(name = "emailTitular", description = "Email of professor", example = "andrei74c0@gmail.com")
    ResponseEntity<?> getArchived(Pageable pageable,
                             @RequestParam(name = "cod", required = false) String cod,
                             @RequestParam(name = "numeDisciplina", required = false) String nume,
                             @RequestParam(name = "anStudiu", required = false) Integer anStudiu,
                             @RequestParam(name = "tipDisciplina",required = false) TipDisciplina tipDisciplina,
                             @RequestParam(name = "categorie", required = false) Categorie categorie,
                             @RequestParam(name = "tipExaminare", required = false) TipExaminare tipExaminare,
                             @RequestParam(name = "numePrenumeTitular", required = false) String numePrenumeTitular,
                             @RequestParam(name = "emailTitular", required = false) String emailTitular
    ) {

        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        Page<Disciplina> page = service.disciplinaSearch(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular, true);

        List<EntityModel<DisciplinaDto>> discipline = page.getContent().stream()
                .map(assembler::toModel)
                .toList();

        PagedModel<EntityModel<DisciplinaDto>> pagedModel = PagedModel.of(
                discipline,
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                                .queryParam("page", page.getNumber())
                                .queryParam("size", page.getSize())
                                .toUriString())
                        .withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() - 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                        .queryParam("page",0)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(DisciplinaController.class).getAll(pageable, cod, nume, anStudiu, tipDisciplina, categorie, tipExaminare, numePrenumeTitular, emailTitular)).toUriComponentsBuilder()
                        .queryParam("page",page.getTotalPages() - 1)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("last")
        );

        return ResponseEntity.ok(pagedModel);
    }

}
