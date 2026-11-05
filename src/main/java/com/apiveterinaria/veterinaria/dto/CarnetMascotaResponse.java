package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Mascota;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record CarnetMascotaResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Rocky")
        String nombre,

        @Schema(example = "Canino")
        String especie,

        @Schema(example = "Labrador")
        String raza,

        @Schema(example = "2023-05-10")
        LocalDate fechaNacimiento
) {
    public static CarnetMascotaResponse from(Mascota m) {
        if (m == null) return null;
        String especie = (m.getRaza() != null && m.getRaza().getEspecie() != null)
                ? m.getRaza().getEspecie().getNombre()
                : null;
        String raza = m.getRaza() != null ? m.getRaza().getNombre() : null;
        return new CarnetMascotaResponse(
                m.getId(),
                m.getNombre(),
                especie,
                raza,
                m.getFechaNacimiento()
        );
    }
}
