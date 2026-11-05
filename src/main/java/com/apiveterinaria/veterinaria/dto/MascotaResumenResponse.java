package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Mascota;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record MascotaResumenResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Rocky")
        String nombre
) {
    public static MascotaResumenResponse from(Mascota m) {
        if (m == null) return null;
        return new MascotaResumenResponse(m.getId(), m.getNombre());
    }
}
