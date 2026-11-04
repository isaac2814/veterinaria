package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Especie;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record EspecieResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "CANINO")
        String codigo,

        @Schema(example = "Canino")
        String nombre
) {
    public static EspecieResumenResponse from(Especie especie) {
        if (especie == null) return null;
        return new EspecieResumenResponse(
                especie.getId(),
                especie.getCodigo(),
                especie.getNombre()
        );
    }
}
