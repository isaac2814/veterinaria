package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarEstadoNotificacionRequest(
        @NotBlank(message = "El nombre del estado es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Enviado")
        String nombre,

        @Schema(example = "Notificación transmitida satisfactoriamente")
        String descripcion
) {
}
