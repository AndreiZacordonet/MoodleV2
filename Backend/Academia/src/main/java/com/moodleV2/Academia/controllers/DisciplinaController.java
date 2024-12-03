package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.dto.DisciplinaDto;
import com.moodleV2.Academia.dto.DisciplinaDtoCreateNew;
import com.moodleV2.Academia.exceptions.DisciplinaNotFoundException;
import com.moodleV2.Academia.exceptions.LengthPaginationException;
import com.moodleV2.Academia.models.Categorie;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.models.TipDisciplina;
import com.moodleV2.Academia.models.TipExaminare;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import com.moodleV2.Academia.service.DisciplinaService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/discipline")
    @Parameter(name = "page", description = "Page number, starting at 0", example = "0")
    @Parameter(name = "size", description = "Number of items per page", example = "10")
    @Parameter(name = "sort", description = "Sort criteria: numeDisciplina, cod, anStudiu, categorie, tipExaminare, tipDisciplina, asc, desc", example = "numeDisciplina,asc")
    ResponseEntity<?> getAll(Pageable pageable,
                             @RequestParam(name = "cod", required = false) String cod,
                             @RequestParam(name = "numeDisciplina", required = false) String nume,
                             @RequestParam(name = "anStudiu", required = false) Integer anStudiu,
                             @RequestParam(name = "tipDisciplina",required = false) TipDisciplina tipDisciplina,
                             @RequestParam(name = "categorie", required = false) Categorie categorie,
                             @RequestParam(name = "tipExaminare", required = false) TipExaminare tipExaminare,
                             @RequestParam(name = "numePrenumeTitular", required = false) String numePrenumeTitular,
                             @RequestParam(name = "emailTitular", required = false) String emailTitular) {

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


    @GetMapping("/discipline/{code}")
    ResponseEntity<?> getByCode(@PathVariable String code) {

        Disciplina disciplina = repository.findById(code)
                .orElseThrow(() -> new DisciplinaNotFoundException(code));

        if (disciplina.isArhivat()) {
            throw new DisciplinaNotFoundException(code);
        }

        return ResponseEntity.ok(assembler.toModel(disciplina));
    }


    @PutMapping("/discipline")
    ResponseEntity<?> createNew(@Valid @RequestBody DisciplinaDtoCreateNew newDisciplina) {

        EntityModel<DisciplinaDto> disciplinaDtoEntityModel = service.addDisciplina(newDisciplina);

        return ResponseEntity.created(disciplinaDtoEntityModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(disciplinaDtoEntityModel);
    }
}
