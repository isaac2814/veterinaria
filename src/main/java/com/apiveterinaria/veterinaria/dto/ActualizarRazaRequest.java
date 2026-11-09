package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ActualizarRazaRequest(
        @Schema(description = "ID de la especie a la que pertenece la raza (opcional si no se desea modificar)")
        UUID especieId,

        @NotBlank(message = "El nombre de la raza es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Labrador Retriever")
        String nombre,

        @Schema(example = "Raza de tamaño mediano-grande originaria del Reino Unido")
        String descripcion
) {
}
