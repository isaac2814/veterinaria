package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.RegistroHistorico;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record HistorialResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        UsuarioResumenResponse usuarioActor,

        @Schema(example = "INSERT")
        String accion,

        @Schema(example = "registros_vacunacion")
        String entidad,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID entidadId,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime fechaEvento,

        @Schema(example = "{\"estado_registro\": \"ACTIVO\"}")
        String cambios,

        @Schema(example = "EXITOSO")
        String resultado,

        @Schema(example = "OP-12345")
        String idOperacion,

        @Schema(example = "API_REST")
        String origen,

        @Schema(example = "192.168.1.100")
        String ipOrigen,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static HistorialResponse from(RegistroHistorico h) {
        if (h == null) return null;
        UsuarioResumenResponse actor = h.getUsuarioActor() != null
                ? UsuarioResumenResponse.from(h.getUsuarioActor())
                : null;
        return new HistorialResponse(
                h.getId(),
                actor,
                h.getAccion(),
                h.getEntidad(),
                h.getEntidadId(),
                h.getFechaEvento(),
                h.getCambios(),
                h.getResultado(),
                h.getIdOperacion(),
                h.getOrigen(),
                h.getIpOrigen(),
                h.getCreatedAt()
        );
    }
}
