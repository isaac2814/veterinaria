package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Propietario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CarnetPropietarioResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Laura")
        String nombres,

        @Schema(example = "Martínez")
        String apellidos,

        @Schema(example = "+573001234567")
        String telefonoWhatsapp
) {
    public static CarnetPropietarioResponse from(Propietario p) {
        if (p == null) return null;
        return new CarnetPropietarioResponse(
                p.getId(),
                p.getNombres(),
                p.getApellidos(),
                p.getTelefonoWhatsapp()
        );
    }
}
