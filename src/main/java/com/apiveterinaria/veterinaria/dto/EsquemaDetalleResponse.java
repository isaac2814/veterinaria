package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.EsquemaVacunacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EsquemaDetalleResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Parvovirus canino - cachorros")
        String nombre,

        VacunaResumenResponse vacuna,

        EspecieResumenResponse especie,

        EdadTiempoResponse edadInicial,

        @Schema(example = "Esquema básico para cachorros")
        String observaciones,

        @Schema(example = "1")
        Integer version,

        List<DosisResponse> dosis,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime updatedAt
) {
    public static EsquemaDetalleResponse from(EsquemaVacunacion e, List<DosisResponse> dosis) {
        if (e == null) return null;
        EdadTiempoResponse edad = (e.getEdadInicialValor() != null || e.getEdadInicialUnidad() != null)
                ? new EdadTiempoResponse(e.getEdadInicialValor(), e.getEdadInicialUnidad())
                : null;

        return new EsquemaDetalleResponse(
                e.getId(),
                e.getNombre(),
                e.getVacuna() != null ? VacunaResumenResponse.from(e.getVacuna()) : null,
                e.getEspecie() != null ? EspecieResumenResponse.from(e.getEspecie()) : null,
                edad,
                e.getObservaciones(),
                e.getVersion(),
                dosis != null ? dosis : List.of(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
