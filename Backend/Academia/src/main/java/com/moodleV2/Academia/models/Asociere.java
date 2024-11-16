package com.moodleV2.Academia.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipul de asociere")
public enum Asociere {
    TITULAR,
    ASOCIAT,
    EXTERN
}
