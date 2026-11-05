package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.LoteVacuna;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record LoteStockResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "L-PARVO-001")
        String numeroLote,

        @Schema(example = "9.00")
        BigDecimal cantidadDisponible
) {
    public static LoteStockResponse from(LoteVacuna l) {
        if (l == null) return null;
        return new LoteStockResponse(l.getId(), l.getNumeroLote(), l.getCantidadDisponible());
    }
}
