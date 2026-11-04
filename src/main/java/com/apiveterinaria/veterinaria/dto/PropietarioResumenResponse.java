package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Propietario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PropietarioResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "1020304050")
        String documento,

        @Schema(example = "Laura")
        String nombres,

        @Schema(example = "Martínez Ruiz")
        String apellidos,

        @Schema(example = "+573001234567")
        String telefonoWhatsapp
) {
    public static PropietarioResumenResponse from(Propietario p) {
        if (p == null) return null;
        return new PropietarioResumenResponse(
                p.getId(),
                p.getDocumento(),
                p.getNombres(),
                p.getApellidos(),
                p.getTelefonoWhatsapp()
        );
    }
}
