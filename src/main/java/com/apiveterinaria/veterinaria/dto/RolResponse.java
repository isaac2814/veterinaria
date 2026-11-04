package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RolResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "VETERINARIO")
        String codigo,

        @Schema(example = "Veterinario")
        String nombre,

        @Schema(example = "Aplica vacunas y gestiona historias")
        String descripcion,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt
) {
    public static RolResponse from(Rol rol) {
        if (rol == null) return null;
        return new RolResponse(
                rol.getId(),
                rol.getCodigo(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getCreatedAt()
        );
    }
}
