package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.EstadoMascota;
import com.apiveterinaria.veterinaria.enums.Sexo;
import com.apiveterinaria.veterinaria.models.Mascota;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MascotaResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "Max")
        String nombre,

        @Schema(example = "MACHO")
        Sexo sexo,

        @Schema(example = "2023-05-15")
        LocalDate fechaNacimiento,

        @Schema(example = "12.50")
        BigDecimal pesoKg,

        @Schema(example = "ACTIVO")
        EstadoMascota estado,

        PropietarioResumenResponse propietario,

        RazaResumenResponse raza,

        EspecieResumenResponse especie,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime updatedAt
) {
    public static MascotaResponse from(Mascota m) {
        if (m == null) return null;
        return new MascotaResponse(
                m.getId(),
                m.getNombre(),
                m.getSexo(),
                m.getFechaNacimiento(),
                m.getPesoKg(),
                m.getEstado(),
                m.getPropietario() != null ? PropietarioResumenResponse.from(m.getPropietario()) : null,
                m.getRaza() != null ? RazaResumenResponse.from(m.getRaza()) : null,
                (m.getRaza() != null && m.getRaza().getEspecie() != null) ? EspecieResumenResponse.from(m.getRaza().getEspecie()) : null,
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}
