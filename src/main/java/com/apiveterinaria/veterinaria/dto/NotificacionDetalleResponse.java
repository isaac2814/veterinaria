package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import com.apiveterinaria.veterinaria.models.Notificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificacionDetalleResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID registroVacunacionId,

        EstadoNotificacionResponse estado,

        @Schema(example = "WHATSAPP")
        CanalNotificacion canal,

        @Schema(example = "Recordatorio: Rocky tiene pendiente su refuerzo de Parvovirus")
        String mensaje,

        @Schema(example = "NOTIF-2026-11-05-001")
        String claveUnica,

        @Schema(example = "wamid.HBgLM...")
        String idMensajeProveedor,

        @Schema(example = "0")
        Integer intentos,

        @Schema(example = "2026-11-23T08:00:00Z")
        OffsetDateTime enviarEn,

        @Schema(example = "null")
        OffsetDateTime fechaEnvio,

        @Schema(example = "null")
        OffsetDateTime proximoReintento,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime updatedAt
) {
    public static NotificacionDetalleResponse from(Notificacion n) {
        if (n == null) return null;
        UUID regId = n.getRegistroVacunacion() != null ? n.getRegistroVacunacion().getId() : null;
        EstadoNotificacionResponse estadoDto = EstadoNotificacionResponse.from(n.getEstado());

        return new NotificacionDetalleResponse(
                n.getId(),
                regId,
                estadoDto,
                n.getCanal(),
                n.getMensaje(),
                n.getClaveUnica(),
                n.getIdMensajeProveedor(),
                n.getIntentos(),
                n.getEnviarEn(),
                n.getFechaEnvio(),
                n.getProximoReintento(),
                n.getCreatedAt(),
                n.getUpdatedAt()
        );
    }
}
