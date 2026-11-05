package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CrearMovimientoRequest(
        @NotNull(message = "El ID del lote es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID loteId,

        @NotNull(message = "La cantidad es obligatoria")
        @Schema(example = "-1.00", description = "Cantidad con signo, distinta de 0")
        BigDecimal cantidad,

        @NotNull(message = "El ID del usuario es obligatorio")
        @Schema(example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID usuarioId,

        @Schema(example = "Frasco roto durante manipulación", description = "Obligatorio para AJUSTE (mínimo 5 caracteres)")
        String observaciones,

        @Size(max = 100, message = "El idOperacion no puede superar los 100 caracteres")
        @Schema(example = "AJUSTE-20261105-001", description = "Identificador opcional de operación para idempotencia")
        String idOperacion,

        @Schema(example = "AJUSTE", description = "Opcional. Por defecto AJUSTE (único tipo permitido en creación manual)")
        TipoMovimiento tipoMovimiento
) {
}
