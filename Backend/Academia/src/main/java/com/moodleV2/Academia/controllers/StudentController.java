package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.*;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.exceptions.StudentArchivedException;
import com.moodleV2.Academia.exceptions.StudentNotFoundException;
import com.moodleV2.Academia.models.Ciclu;
import com.moodleV2.Academia.models.Student;
import com.moodleV2.Academia.repositories.StudentRepository;
import com.moodleV2.Academia.service.StudentService;
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

import static com.moodleV2.Academia.service.Utils.createPagedModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/academia")
public class StudentController {

    private final StudentService service;
    private final StudentRepository repository;
    private final StudentModelAssembler assembler;

    public StudentController(StudentService service, StudentRepository repository, StudentModelAssembler assembler) {
        this.service = service;
        this.repository = repository;
        this.assembler = assembler;
    }


    @GetMapping("/studenti")
    @Operation(summary = "Retrieve all students information.",
            description = "Specific filtering can be applied, the result is shown in a page based on selected preferences.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Pagination parameters not in range"),
            @ApiResponse(responseCode = "422", description = "Sorting parameters not valid")
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: nume, prenume, email, cicluStudii, anStudiu, grupa, asc, desc", example = "nume,asc")
    @Parameter(name = "nume", description = "Filter by student's last name (partial match allowed). Case-insensitive.", example = "Popescu")
    @Parameter(name = "prenume", description = "Filter by student's first name (partial match allowed). Case-insensitive.", example = "Iongetit")
    @Parameter(name = "email", description = "Filter by student's email address. Must be a valid email format.", example = "ion.popescu@example.com")
    @Parameter(name = "ciclu", description = "Filter by a student's study cycle. Allowed values: LICENTA, MASTER", schema = @Schema(implementation = Ciclu.class))
    @Parameter(name = "an", description = "Filter by student's study year.", example = "4")
    @Parameter(name = "grupa", description = "Filyer by student's study group.", example = "123")
    @Parameter(name = "codDisciplina", description = "Filter by student's class code.", example = "MATH69")
    @Parameter(name = "profId", description = "Filter by a student's professor identifier.",example = "3")
    ResponseEntity<?> getAll(Pageable pageable,
                             @RequestParam(name = "nume", required = false) String nume,
                             @RequestParam(name = "prenume", required = false) String prenume,
                             @RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "ciclu", required = false) Ciclu ciclu,
                             @RequestParam(name = "an", required = false) Integer an,
                             @RequestParam(name = "grupa", required = false) Integer grupa,
                             @RequestParam(name = "codDisciplina", required = false) String codDisciplina,
                             @RequestParam(name = "profId", required = false) Long profId
    ) {

        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        Page<Student> page = service.studentSearch(pageable, nume, prenume, email, ciclu, an, grupa, profId, codDisciplina, false);

        List<EntityModel<StudentDto>> studenti =
                page.getContent().stream()
                        .map(assembler::toModel)
                        .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/academia/studenti");

        PagedModel<EntityModel<StudentDto>> pagedModel = createPagedModel(
                studenti,
                metadata,
                uriBuilder,
                page.getNumber(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(pagedModel);
    }



    @GetMapping("/studenti/{id}")
    @Operation(summary = "Retrieve ONE student full informations.",
            description = "Searches by the provided student identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Student unique code", example = "2")
    ResponseEntity<?> getById(@PathVariable Long id) {

        if (id > 10000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Student student = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id { " + id + " } was not found."));

        if (student.isArhivat()) {
            throw new StudentNotFoundException("Student with id { " + id + " } was not found.");
        }

        return ResponseEntity.ok(assembler.toModel(student));
    }


    @PostMapping("/studenti")
    @Operation(summary = "Create a new student.",
            description = "Using the sent data, checks for its correctness and proceeds to create and store the new student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully", content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "406", description = "Parameter format is not correct"),
            @ApiResponse(responseCode = "409", description = "Student email already exists"),
            @ApiResponse(responseCode = "422", description = "Student data is invalid")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New student data",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StudentDtoCreateNew.class)
            ))
    ResponseEntity<?> createNew(@Validated @RequestBody StudentDtoCreateNew newStudent) {

        EntityModel<StudentDto> studentEntityModel = service.addStudent(newStudent);

        return ResponseEntity.created(studentEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(studentEntityModel);
    }


    @DeleteMapping("/studenti/{id}")
    @Operation(summary = "Archives a specified student.",
            description = "Using the provided code, it will switch the archive flag thus marking it as being archived.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully archived"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Student unique code", example = "2")
    ResponseEntity<?> archiveById(@PathVariable Long id) {

        if (id > 10000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Student student = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id { " + id + " } was not found."));

        if (student.isArhivat()) {
            throw new StudentNotFoundException("Student with id { " + id + " } was not found.");
        }

        student.setArhivat(true);
        repository.save(student);

        return ResponseEntity.ok(assembler.toModel(student));
    }


    @PatchMapping("/studenti/{id}")
    @Operation(summary = "Updates specified student data.",
            description = "Provided fields, if syntactically and logically correct, will be updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "404", description = "Student/Course not found"),
            @ApiResponse(responseCode = "409", description = "Student email already exists"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier"),
            @ApiResponse(responseCode = "422", description = "Student data is invalid")
    })
    @Parameter(name = "id", description = "Student unique code", example = "2")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to be updated. Only the provided fields will be modified.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StudentDtoUpdate.class)
            ))
    ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody StudentDtoUpdate fields) {

        if (id > 10000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Student student = service.partialUpdateStudent(id, fields);

        return ResponseEntity.ok(assembler.toModel(student));
    }


    @PostMapping("/studenti/{id}/activate")
    @Operation(summary = "Removes specified student from archive.",
            description = "If the provided code is correct, marks the course as being active by switching the archive flag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Student unique code", example = "2")
    ResponseEntity<?> removeFromArchiveById(@PathVariable Long id) {

        if (id > 10000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Student student = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id { " + id + " } was not found."));

        if (!student.isArhivat()) {
            throw new StudentArchivedException("Student with id { " + id + " } is NOT archived.");
        }

        student.setArhivat(false);
        repository.save(student);

        return ResponseEntity.ok(assembler.toModel(student));
    }


    @GetMapping("/studenti/archive")
    @Operation(summary = "Retrieve all ARCHIVED students information.",
            description = "Specific filtering can be applied, the result is shown in a page based on selected preferences.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Professor not found"),
            @ApiResponse(responseCode = "416", description = "Pagination parameters not in range"),
            @ApiResponse(responseCode = "422", description = "Sorting parameters not valid")
    })
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: nume, prenume, email, cicluStudii, anStudiu, grupa, asc, desc", example = "nume,asc")
    @Parameter(name = "nume", description = "Filter by student's last name (partial match allowed). Case-insensitive.", example = "Popescu")
    @Parameter(name = "prenume", description = "Filter by student's first name (partial match allowed). Case-insensitive.", example = "Iongetit")
    @Parameter(name = "email", description = "Filter by student's email address. Must be a valid email format.", example = "ion.popescu@example.com")
    @Parameter(name = "ciclu", description = "Filter by a student's study cycle. Allowed values: LICENTA, MASTER", schema = @Schema(implementation = Ciclu.class))
    @Parameter(name = "an", description = "Filter by student's study year.", example = "4")
    @Parameter(name = "grupa", description = "Filyer by student's study group.", example = "123")
    @Parameter(name = "codDisciplina", description = "Filter by student's class code.", example = "MATH69")
    @Parameter(name = "profId", description = "Filter by a student's professor identifier.",example = "3")
    ResponseEntity<?> getArchived(Pageable pageable,
                             @RequestParam(name = "nume", required = false) String nume,
                             @RequestParam(name = "prenume", required = false) String prenume,
                             @RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "ciclu", required = false) Ciclu ciclu,
                             @RequestParam(name = "an", required = false) Integer an,
                             @RequestParam(name = "grupa", required = false) Integer grupa,
                             @RequestParam(name = "codDisciplina", required = false) String codDisciplina,
                             @RequestParam(name = "profId", required = false) Long profId
    ) {

        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        Page<Student> page = service.studentSearch(pageable, nume, prenume, email, ciclu, an, grupa, profId, codDisciplina, true);

        List<EntityModel<StudentDto>> studenti =
                page.getContent().stream()
                        .map(assembler::toModel)
                        .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/academia/studenti/archive");

        PagedModel<EntityModel<StudentDto>> pagedModel = createPagedModel(
                studenti,
                metadata,
                uriBuilder,
                page.getNumber(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/studenti/{id}/disciplines")
    @Operation(summary = "Retrieve a student's courses.",
            description = "Retrieve all courses specified by a student identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully un-archived"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "416", description = "Invalid identifier")
    })
    @Parameter(name = "id", description = "Student unique code", example = "2")
    ResponseEntity<?> getMyDisciplines(@PathVariable Long id) {

        List<EntityModel<DisciplinaDto>> disciplines = service.getMyDisciplines(id);

        CollectionModel<EntityModel<DisciplinaDto>> collectionModel = CollectionModel.of(disciplines);

        collectionModel.add(Link.of("/api/academia/discipline").withRel("discipline").withType("GET"));
        collectionModel.add(linkTo(methodOn(StudentController.class).getMyDisciplines(id)).withSelfRel().withType("GET"));
        collectionModel.add(linkTo(methodOn(StudentController.class).getById(id)).withRel("student").withType("GET"));

        return ResponseEntity.ok(collectionModel);
    }

}
