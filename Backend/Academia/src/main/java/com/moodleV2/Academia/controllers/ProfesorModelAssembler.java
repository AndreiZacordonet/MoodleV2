package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.models.Profesor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProfesorModelAssembler implements RepresentationModelAssembler<Profesor, EntityModel<ProfesorDto>> {
    @Override
    public EntityModel<ProfesorDto> toModel(Profesor profesor) {
        ProfesorDto profesorDto = new ProfesorDto(
                profesor.getNume(),
                profesor.getPrenume(),
                profesor.getEmail(),
                profesor.getGradDidactic(),
                profesor.getTipAsociere(),
                profesor.getAfiliere()
        );
//        System.out.println(profesorDto.toString());
        return EntityModel.of(profesorDto,
                linkTo(methodOn(ProfesorController.class).getById(profesor.getId())).withSelfRel().withType("GET"),
                linkTo(methodOn(ProfesorController.class).getAll(PageRequest.of(0, 10))).withRel("profesori").withType("GET"));
    }
}
