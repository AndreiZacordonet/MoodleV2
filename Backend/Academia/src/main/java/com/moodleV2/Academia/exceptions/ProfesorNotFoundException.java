package com.moodleV2.Academia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find profesor")
public class ProfesorNotFoundException extends RuntimeException {
    public ProfesorNotFoundException(Long id) {
        super("Could not find profesor " + id);
    }
}
