package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CrearRazaRequest(
        @NotNull(message = "El ID de la especie es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID especieId,

        @NotBlank(message = "El nombre de la raza es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Golden Retriever")
        String nombre,

        @Schema(example = "Raza de tamaño mediano-grande originaria del Reino Unido")
        String descripcion
) {
}
