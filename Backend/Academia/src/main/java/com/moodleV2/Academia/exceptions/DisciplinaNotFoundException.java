package com.moodleV2.Academia.exceptions;

import com.moodleV2.Academia.models.Disciplina;

public class DisciplinaNotFoundException extends RuntimeException{
    public DisciplinaNotFoundException(String msg) {
        super(msg);
    }
}
