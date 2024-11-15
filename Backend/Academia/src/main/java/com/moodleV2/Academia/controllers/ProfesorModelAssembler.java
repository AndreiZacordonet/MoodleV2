package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.models.Profesor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProfesorModelAssembler implements RepresentationModelAssembler<Profesor, EntityModel<Profesor>> {
    @Override
    public EntityModel<Profesor> toModel(Profesor profesor) {
        return EntityModel.of(profesor,
                linkTo(methodOn(ProfesorController.class).getById(profesor.getId())).withSelfRel().withType("GET"),
                linkTo(methodOn(ProfesorController.class).getAll()).withRel("profesori").withType("GET"));
    }
}
