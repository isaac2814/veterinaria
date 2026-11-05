package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CrearLoteRequest(
        @NotNull(message = "El ID de la vacuna es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID vacunaId,

        @NotBlank(message = "El número de lote es obligatorio")
        @Size(max = 100, message = "El número de lote no puede superar los 100 caracteres")
        @Schema(example = "L-PARVO-001")
        String numeroLote,

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        @Schema(example = "2027-11-05")
        LocalDate fechaVencimiento,

        @Schema(example = "2026-11-05", description = "Opcional. Por defecto la BD usa hoy")
        LocalDate fechaIngreso,

        @NotNull(message = "La cantidad recibida es obligatoria")
        @DecimalMin(value = "0.01", inclusive = true, message = "La cantidad recibida debe ser mayor a 0")
        @Schema(example = "10.00")
        BigDecimal cantidadRecibida,

        @Size(max = 50, message = "La unidad de inventario no puede superar los 50 caracteres")
        @Schema(example = "DOSIS", description = "Opcional. Por defecto 'DOSIS'")
        String unidadInventario,

        @NotNull(message = "El ID del usuario que registra el lote es obligatorio")
        @Schema(example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID registradoPor
) {
}
