package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

public record AnulacionResponse(
        @Schema(example = "Aplicada a la mascota equivocada")
        String motivo,

        UsuarioResumenResponse anuladoPor,

        @Schema(example = "2026-11-05T14:30:00Z")
        OffsetDateTime fecha
) {
}
