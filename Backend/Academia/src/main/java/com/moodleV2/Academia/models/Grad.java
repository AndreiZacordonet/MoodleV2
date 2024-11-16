package com.moodleV2.Academia.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Gradul profesorului")
public enum Grad {
    ASISTENT,
    SEF_LUCRARI,
    CONFERENTIAR,
    PROFESOR
}
