package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearEstadoNotificacionRequest(
        @NotBlank(message = "El nombre del estado es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Entregado")
        String nombre,

        @Schema(example = "ENTREGADO", description = "Código único del estado. Si no se envía se generará a partir del nombre.")
        String codigo,

        @Schema(example = "Mensaje entregado exitosamente al destinatario")
        String descripcion
) {
}
