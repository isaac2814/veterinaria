package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActualizarNotificacionRequest(
        @Schema(example = "Recordatorio: Se aproxima la fecha de refuerzo de la vacuna de su mascota")
        String mensaje,

        @Schema(example = "2026-11-20T10:00:00Z")
        OffsetDateTime enviarEn,

        @Schema(example = "WHATSAPP")
        CanalNotificacion canal,

        @Schema(description = "ID del nuevo estado de la notificación")
        UUID estadoId
) {
}
