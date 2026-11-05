package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.LoteVacuna;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record LoteResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        VacunaResumenResponse vacuna,

        @Schema(example = "L-PARVO-001")
        String numeroLote,

        @Schema(example = "2027-11-05")
        LocalDate fechaVencimiento,

        @Schema(example = "2026-11-05")
        LocalDate fechaIngreso,

        @Schema(example = "10.00")
        BigDecimal cantidadRecibida,

        @Schema(example = "10.00")
        BigDecimal cantidadDisponible,

        @Schema(example = "DOSIS")
        String unidadInventario,

        UsuarioResumenResponse registradoPor,

        @Schema(example = "false")
        boolean vencido,

        @Schema(example = "365", description = "Días restantes para vencer. Negativo si ya venció")
        long diasParaVencer,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime updatedAt
) {
    public static LoteResponse from(LoteVacuna l, LocalDate hoy) {
        if (l == null) return null;
        boolean vencido = l.getFechaVencimiento().isBefore(hoy);
        long dias = ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento());
        return new LoteResponse(
                l.getId(),
                VacunaResumenResponse.from(l.getVacuna()),
                l.getNumeroLote(),
                l.getFechaVencimiento(),
                l.getFechaIngreso(),
                l.getCantidadRecibida(),
                l.getCantidadDisponible(),
                l.getUnidadInventario(),
                UsuarioResumenResponse.from(l.getRegistradoPor()),
                vencido,
                dias,
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}
