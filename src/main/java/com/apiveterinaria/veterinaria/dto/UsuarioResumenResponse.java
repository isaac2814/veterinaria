package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UsuarioResumenResponse(
        @Schema(example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID id,

        @Schema(example = "Carlos")
        String nombre,

        @Schema(example = "Gómez")
        String apellidos
) {
    public static UsuarioResumenResponse from(Usuario u) {
        if (u == null) return null;
        return new UsuarioResumenResponse(
                u.getId(),
                u.getNombre(),
                u.getApellidos()
        );
    }
}
