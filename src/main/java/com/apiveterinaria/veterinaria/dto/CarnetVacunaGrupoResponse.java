package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CarnetVacunaGrupoResponse(
        @Schema(description = "Datos de la vacuna")
        VacunaResumenResponse vacuna,

        @Schema(description = "Historial de aplicaciones de esta vacuna, ordenadas por fecha")
        List<CarnetAplicacionResponse> aplicaciones
) {
}
