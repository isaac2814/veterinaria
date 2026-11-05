package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearRolRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "AUXILIAR_VETERINARIO")
        String nombre,

        @Schema(example = "AUXILIAR", description = "Código único del rol. Si no se envía se generará a partir del nombre.")
        String codigo,

        @Schema(example = "Personal auxiliar de clínica y soporte veterinario")
        String descripcion
) {
}
