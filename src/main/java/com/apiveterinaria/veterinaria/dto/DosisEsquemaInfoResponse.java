package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.DosisEsquema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record DosisEsquemaInfoResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "1")
        Integer numeroDosis,

        @Schema(example = "Esquema básico para cachorros")
        String esquema
) {
    public static DosisEsquemaInfoResponse from(DosisEsquema d) {
        if (d == null) return null;
        String nombreEsquema = d.getEsquema() != null ? d.getEsquema().getNombre() : null;
        return new DosisEsquemaInfoResponse(d.getId(), d.getNumeroDosis(), nombreEsquema);
    }
}
