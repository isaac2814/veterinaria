package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Vacuna;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record VacunaSimpleResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Parvovirus")
        String nombre
) {
    public static VacunaSimpleResponse from(Vacuna v) {
        if (v == null) return null;
        return new VacunaSimpleResponse(
                v.getId(),
                v.getNombre()
        );
    }
}
