package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.dto.StudentDto;
import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.models.Student;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class StudentModelAssembler implements RepresentationModelAssembler<Student, EntityModel<StudentDto>> {

    @Override
    public EntityModel<StudentDto> toModel(Student student) {
        StudentDto studentDto = new StudentDto(student);

        return EntityModel.of(studentDto,
                Link.of("/v3/api-docs").withRel("api-docs").withTitle("API Documentation"));
    }
}
