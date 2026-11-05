package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record CarnetProximoRefuerzoResponse(
        @Schema(example = "Parvovirus")
        String vacuna,

        @Schema(example = "2026-11-26")
        LocalDate fechaProximoRefuerzo,

        @Schema(example = "21", description = "Días restantes para el refuerzo. Negativo si ya está atrasado")
        long diasRestantes
) {
}
