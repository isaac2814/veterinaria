package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import com.apiveterinaria.veterinaria.models.DosisEsquema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DosisResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID esquemaId,

        @Schema(example = "1")
        Integer numeroDosis,

        @Schema(example = "3")
        Integer intervaloCantidad,

        @Schema(example = "SEMANAS")
        UnidadTiempo intervaloUnidad,

        @Schema(example = "6")
        Integer edadRecomendadaValor,

        @Schema(example = "SEMANAS")
        UnidadTiempo edadRecomendadaUnidad,

        @Schema(example = "false")
        Boolean esRefuerzo,

        @Schema(example = "Primera dosis del esquema")
        String observaciones,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime updatedAt
) {
    public static DosisResponse from(DosisEsquema d) {
        if (d == null) return null;
        return new DosisResponse(
                d.getId(),
                d.getEsquema() != null ? d.getEsquema().getId() : null,
                d.getNumeroDosis(),
                d.getIntervaloCantidad(),
                d.getIntervaloUnidad(),
                d.getEdadRecomendadaValor(),
                d.getEdadRecomendadaUnidad(),
                d.getEsRefuerzo(),
                d.getObservaciones(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
}
