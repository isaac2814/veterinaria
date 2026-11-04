package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UsuarioResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Carlos")
        String nombre,

        @Schema(example = "Gómez Pérez")
        String apellidos,

        @Schema(example = "carlos.gomez@veterinaria.com")
        String email,

        RolResumenResponse rol,

        @Schema(example = "true")
        Boolean activo,

        @Schema(example = "2026-10-29T20:15:00Z")
        OffsetDateTime ultimoAcceso,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime updatedAt
) {
    public static UsuarioResponse from(Usuario u) {
        if (u == null) return null;
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getApellidos(),
                u.getEmail(),
                u.getRol() != null ? RolResumenResponse.from(u.getRol()) : null,
                u.getActivo(),
                u.getUltimoAcceso(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
