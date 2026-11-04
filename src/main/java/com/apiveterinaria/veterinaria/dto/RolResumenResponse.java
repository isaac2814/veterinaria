package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RolResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "VETERINARIO")
        String codigo,

        @Schema(example = "Veterinario")
        String nombre
) {
    public static RolResumenResponse from(Rol rol) {
        if (rol == null) return null;
        return new RolResumenResponse(
                rol.getId(),
                rol.getCodigo(),
                rol.getNombre()
        );
    }
}
