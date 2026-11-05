package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.LoteVacuna;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record LoteResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "L-PARVO-001")
        String numeroLote
) {
    public static LoteResumenResponse from(LoteVacuna l) {
        if (l == null) return null;
        return new LoteResumenResponse(
                l.getId(),
                l.getNumeroLote()
        );
    }
}
