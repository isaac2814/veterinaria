package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActualizarLoteRequest(
        @NotBlank(message = "El número de lote es obligatorio")
        @Size(max = 100, message = "El número de lote no puede superar los 100 caracteres")
        @Schema(example = "L-PARVO-001-REV")
        String numeroLote,

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        @Schema(example = "2027-12-31")
        LocalDate fechaVencimiento,

        @Size(max = 50, message = "La unidad de inventario no puede superar los 50 caracteres")
        @Schema(example = "DOSIS")
        String unidadInventario
) {
}
