package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Raza;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RazaResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Labrador Retriever")
        String nombre
) {
    public static RazaResumenResponse from(Raza raza) {
        if (raza == null) return null;
        return new RazaResumenResponse(
                raza.getId(),
                raza.getNombre()
        );
    }
}
