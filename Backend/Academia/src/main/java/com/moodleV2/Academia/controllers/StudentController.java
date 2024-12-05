package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.StudentDto;
import com.moodleV2.Academia.dto.StudentDtoCreateNew;
import com.moodleV2.Academia.dto.StudentDtoUpdate;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.exceptions.StudentNotFoundException;
import com.moodleV2.Academia.models.Ciclu;
import com.moodleV2.Academia.models.Student;
import com.moodleV2.Academia.repositories.StudentRepository;
import com.moodleV2.Academia.service.StudentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

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
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: nume, prenume, email, cicluStudii, anStudiu, grupa, asc, desc", example = "nume,asc")
    ResponseEntity<?> getAll(Pageable pageable,
                             @RequestParam(name = "nume", required = false) String nume,
                             @RequestParam(name = "prenume", required = false) String prenume,
                             @RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "ciclu", required = false) Ciclu ciclu,
                             @RequestParam(name = "an", required = false) Integer an,
                             @RequestParam(name = "grupa", required = false) Integer grupa
                             ) {

        if (pageable.getPageNumber() > 1000 || pageable.getPageSize() > 30) {
            // default values for page and size (0, 20) override any errors like assigning a string or a negative number
            throw new LengthPaginationException();
        }

        Page<Student> page = service.studentSearch(pageable, nume, prenume, email, ciclu, an, grupa, false);

        List<EntityModel<StudentDto>> studenti =
                page.getContent().stream()
                        .map(assembler::toModel)
                        .toList();

        PagedModel<EntityModel<StudentDto>> pagedModel = PagedModel.of(
                studenti,
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                Link.of(linkTo(methodOn(StudentController.class).getAll(pageable, nume, prenume, email, ciclu, an, grupa)).toUriComponentsBuilder()
                                .queryParam("page", page.getNumber())
                                .queryParam("size", page.getSize())
                                .toUriString())
                        .withSelfRel()
        );

        if (page.hasPrevious()) {
            pagedModel.add(Link.of(linkTo(methodOn(StudentController.class).getAll(pageable, nume, prenume, email, ciclu, an, grupa)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() - 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("prev")
            );
        }

        if (page.hasNext()) {
            pagedModel.add(Link.of(linkTo(methodOn(StudentController.class).getAll(pageable, nume, prenume, email, ciclu, an, grupa)).toUriComponentsBuilder()
                            .queryParam("page",pageable.getPageNumber() + 1)
                            .queryParam("size", pageable.getPageSize())
                            .toUriString())
                    .withRel("next")
            );
        }

        // adding first page link
        pagedModel.add(Link.of(linkTo(methodOn(StudentController.class).getAll(pageable, nume, prenume, email, ciclu, an, grupa)).toUriComponentsBuilder()
                        .queryParam("page",0)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("first")
        );

        // adding last page link
        pagedModel.add(Link.of(linkTo(methodOn(StudentController.class).getAll(pageable, nume, prenume, email, ciclu, an, grupa)).toUriComponentsBuilder()
                        .queryParam("page",page.getTotalPages() - 1)
                        .queryParam("size", pageable.getPageSize())
                        .toUriString())
                .withRel("last")
        );

        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/studenti/{id}")
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
    ResponseEntity<?> createNew(@Validated @RequestBody StudentDtoCreateNew newStudent) {

        EntityModel<StudentDto> studentEntityModel = service.addStudent(newStudent);

        return ResponseEntity.created(studentEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(studentEntityModel);
    }


    @DeleteMapping("/studenti/{id}")
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
    ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody StudentDtoUpdate fields) {

        if (id > 10000 || id < 0) {
            throw new IndexOutOfBoundsException();
        }

        Student student = service.partialUpdateStudent(id, fields);

        return ResponseEntity.ok(assembler.toModel(student));
    }

    // TODO: get student courses

}
