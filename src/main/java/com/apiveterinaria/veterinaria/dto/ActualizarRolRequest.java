package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarRolRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Veterinario Especialista")
        String nombre,

        @Schema(example = "Rol con permisos clínicos y quirúrgicos avanzados")
        String descripcion
) {
}
