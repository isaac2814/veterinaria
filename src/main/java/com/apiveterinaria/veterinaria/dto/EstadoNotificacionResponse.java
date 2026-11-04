package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.EstadoNotificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record EstadoNotificacionResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "PENDIENTE")
        String codigo,

        @Schema(example = "Pendiente")
        String nombre,

        @Schema(example = "Esperando ser enviada")
        String descripcion
) {
    public static EstadoNotificacionResponse from(EstadoNotificacion estado) {
        if (estado == null) return null;
        return new EstadoNotificacionResponse(
                estado.getId(),
                estado.getCodigo(),
                estado.getNombre(),
                estado.getDescripcion()
        );
    }
}
