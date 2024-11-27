package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.dto.ProfesorDto;
import com.moodleV2.Academia.models.Disciplina;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DisciplinaModelAssembler implements RepresentationModelAssembler<Disciplina, EntityModel<DisciplinaDto>> {

    @Override
    public EntityModel<DisciplinaDto> toModel(Disciplina disciplina) {
        DisciplinaDto disciplinaDto = new DisciplinaDto(
                disciplina.getCod(),
                new ProfesorDto(disciplina.getIdTitular()),
                disciplina.getNumeDisciplina(),
                disciplina.getAnStudiu(),
                disciplina.getTipDisciplina(),
                disciplina.getCategorie(),
                disciplina.getTipExaminare()
        );

        return EntityModel.of(disciplinaDto,
                linkTo(methodOn(DisciplinaController.class).getAll()).withSelfRel());
    }
}
