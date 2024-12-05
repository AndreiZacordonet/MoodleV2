package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.StudentDto;
import com.moodleV2.Academia.models.Student;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StudentModelAssembler implements RepresentationModelAssembler<Student, EntityModel<StudentDto>> {

    @Override
    public EntityModel<StudentDto> toModel(Student student) {
        StudentDto studentDto = new StudentDto(student);

        return EntityModel.of(studentDto,
                linkTo(methodOn(StudentController.class).getById(student.getId())).withSelfRel().withType("GET"),
                linkTo(methodOn(StudentController.class).getAll(PageRequest.of(0, 10), null, null, null, null, null, null)).withRel("studenti").withType("GET"),
                Link.of("/v3/api-docs").withRel("api-docs").withTitle("API Documentation"));
    }
}
