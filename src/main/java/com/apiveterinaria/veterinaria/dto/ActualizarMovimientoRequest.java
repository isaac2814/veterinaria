package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarMovimientoRequest(
        @NotBlank(message = "Las observaciones son obligatorias")
        @Size(min = 5, max = 500, message = "Las observaciones deben tener entre 5 y 500 caracteres")
        @Schema(example = "Ajuste de inventario justificado por conteo físico periódico")
        String observaciones
) {
}
