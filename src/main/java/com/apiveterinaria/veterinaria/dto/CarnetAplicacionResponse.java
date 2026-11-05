package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.models.RegistroVacunacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record CarnetAplicacionResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID registroId,

        @Schema(example = "1")
        Integer numeroDosis,

        @Schema(example = "2026-11-05")
        LocalDate fechaAplicacion,

        @Schema(example = "L-PARVO-001")
        String lote,

        @Schema(example = "Carlos Gómez")
        String veterinario,

        @Schema(example = "2026-11-26")
        LocalDate fechaProximoRefuerzo,

        @Schema(example = "COMPLETADO")
        EstadoRefuerzo estadoRefuerzo,

        @Schema(example = "ACTIVO")
        EstadoRegistro estadoRegistro
) {
    public static CarnetAplicacionResponse from(RegistroVacunacion r) {
        if (r == null) return null;
        String numeroLote = r.getLoteVacuna() != null ? r.getLoteVacuna().getNumeroLote() : null;
        String vetNombre = r.getVeterinario() != null
                ? (r.getVeterinario().getNombre() + " " + r.getVeterinario().getApellidos()).trim()
                : null;
        return new CarnetAplicacionResponse(
                r.getId(),
                r.getNumeroDosis(),
                r.getFechaAplicacion(),
                numeroLote,
                vetNombre,
                r.getFechaProximoRefuerzo(),
                r.getEstadoRefuerzo(),
                r.getEstadoRegistro()
        );
    }
}
