package com.moodleV2.Academia.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categoria disciplinei")
public enum Categorie {
    DOMENIU,
    SPECIALITATE,
    ADIACENTA
}
