package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarEspecieRequest(
        @NotBlank(message = "El nombre de la especie es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Canino")
        String nombre,

        @Schema(example = "Caninos domésticos y de compañía")
        String descripcion
) {
}
