package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.TipoMovimiento;
import com.apiveterinaria.veterinaria.models.MovimientoInventario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MovimientoResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "AJUSTE")
        TipoMovimiento tipoMovimiento,

        @Schema(example = "-1.00")
        BigDecimal cantidad,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime fechaMovimiento,

        LoteResumenResponse lote,

        VacunaSimpleResponse vacuna,

        @Schema(example = "null")
        UUID registroVacunacionId,

        UsuarioResumenResponse usuario,

        @Schema(example = "Frasco roto durante manipulación")
        String observaciones,

        @Schema(example = "AJUSTE-20261105-001")
        String idOperacion
) {
    public static MovimientoResponse from(MovimientoInventario m) {
        if (m == null) return null;
        return new MovimientoResponse(
                m.getId(),
                m.getTipoMovimiento(),
                m.getCantidad(),
                m.getFechaMovimiento(),
                LoteResumenResponse.from(m.getLote()),
                m.getLote() != null ? VacunaSimpleResponse.from(m.getLote().getVacuna()) : null,
                m.getRegistroVacunacionId(),
                UsuarioResumenResponse.from(m.getUsuario()),
                m.getObservaciones(),
                m.getIdOperacion()
        );
    }
}
