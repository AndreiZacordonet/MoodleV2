package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.models.Profesor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Sort;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProfesorModelAssembler implements RepresentationModelAssembler<Profesor, EntityModel<ProfesorDto>> {
    @Override
    public EntityModel<ProfesorDto> toModel(Profesor profesor) {
        ProfesorDto profesorDto = new ProfesorDto(profesor);

        return EntityModel.of(profesorDto,
                linkTo(methodOn(ProfesorController.class).getById(profesor.getId())).withSelfRel().withType("GET"),
                linkTo(methodOn(ProfesorController.class).getAll(PageRequest.of(0, 10), null, null, null, null, null, null, null)).withRel("profesori").withType("GET"),
                Link.of("/v3/api-docs").withRel("api-docs").withTitle("API Documentation"));
    }
}
