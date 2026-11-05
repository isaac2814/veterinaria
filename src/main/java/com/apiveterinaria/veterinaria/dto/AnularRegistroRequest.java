package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AnularRegistroRequest(
        @NotBlank(message = "El motivo de anulación es obligatorio")
        @Size(min = 5, message = "El motivo de anulación debe tener al menos 5 caracteres")
        @Schema(example = "Aplicada a la mascota equivocada")
        String motivo,

        @NotNull(message = "El ID del usuario que anula es obligatorio")
        @Schema(example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID usuarioId
) {
}
