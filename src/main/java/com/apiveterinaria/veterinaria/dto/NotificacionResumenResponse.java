package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import com.apiveterinaria.veterinaria.models.Notificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificacionResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "WHATSAPP")
        CanalNotificacion canal,

        @Schema(example = "PENDIENTE")
        String estado,

        @Schema(example = "Recordatorio: Rocky tiene pendiente su refuerzo de Parvovirus el 2026-11-26.")
        String mensaje,

        @Schema(example = "2026-11-23T08:00:00Z")
        OffsetDateTime enviarEn,

        @Schema(example = "null")
        OffsetDateTime fechaEnvio,

        @Schema(example = "0")
        Integer intentos
) {
    public static NotificacionResumenResponse from(Notificacion n) {
        if (n == null) return null;
        String estadoCodigo = n.getEstado() != null ? n.getEstado().getCodigo() : null;
        return new NotificacionResumenResponse(
                n.getId(),
                n.getCanal(),
                estadoCodigo,
                n.getMensaje(),
                n.getEnviarEn(),
                n.getFechaEnvio(),
                n.getIntentos()
        );
    }
}
