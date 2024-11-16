package com.moodleV2.Academia.utils;

import com.moodleV2.Academia.models.Profesor;
import com.moodleV2.Academia.repositories.ProfesorRepository;

public class DataCheck {

    private final ProfesorRepository profesorRepository;

    public DataCheck(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    public boolean ProfesorCheck(Profesor profesor) {
        return false;
    }
}
