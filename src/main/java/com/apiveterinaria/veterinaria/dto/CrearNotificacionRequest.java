package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CrearNotificacionRequest(
        @NotNull(message = "El ID del registro de vacunación es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID registroVacunacionId,

        @NotNull(message = "El canal de notificación es obligatorio (WHATSAPP, EMAIL, LLAMADA)")
        @Schema(example = "WHATSAPP")
        CanalNotificacion canal,

        @NotBlank(message = "El mensaje es obligatorio")
        @Schema(example = "Recordatorio: Rocky tiene pendiente su refuerzo de Parvovirus")
        String mensaje,

        @Schema(example = "2026-11-23T08:00:00Z", description = "Fecha y hora programada para envío")
        OffsetDateTime enviarEn
) {
}
