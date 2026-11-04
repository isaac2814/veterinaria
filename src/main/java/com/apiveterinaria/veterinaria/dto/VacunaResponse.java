package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Vacuna;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record VacunaResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "PARVO")
        String codigo,

        @Schema(example = "Parvovirus")
        String nombre,

        @Schema(example = "Zoetis")
        String fabricante,

        @Schema(example = "Vacuna viva modificada contra el parvovirus canino")
        String descripcion,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime updatedAt
) {
    public static VacunaResponse from(Vacuna v) {
        if (v == null) return null;
        return new VacunaResponse(
                v.getId(),
                v.getCodigo(),
                v.getNombre(),
                v.getFabricante(),
                v.getDescripcion(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }
}
