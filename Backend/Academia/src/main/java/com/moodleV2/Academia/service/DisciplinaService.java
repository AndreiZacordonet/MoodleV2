package com.moodleV2.Academia.service;

import com.moodleV2.Academia.controllers.DisciplinaModelAssembler;
import com.moodleV2.Academia.models.Disciplina;
import com.moodleV2.Academia.repositories.DisciplinaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaModelAssembler assembler;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, DisciplinaModelAssembler assembler) {
        this.disciplinaRepository = disciplinaRepository;
        this.assembler = assembler;
    }

//    public Page<Disciplina> DisciplinaSearch(Pageable pageable,
//                                             String cod, )
}
