package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearEspecieRequest(
        @NotBlank(message = "El nombre de la especie es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Ave")
        String nombre,

        @Schema(example = "AVE", description = "Código único de la especie. Si no se envía se generará a partir del nombre.")
        String codigo,

        @Schema(example = "Aves domésticas y de compañía")
        String descripcion
) {
}
