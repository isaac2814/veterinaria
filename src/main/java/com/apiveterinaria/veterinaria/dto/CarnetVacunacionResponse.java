package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CarnetVacunacionResponse(
        CarnetMascotaResponse mascota,

        CarnetPropietarioResponse propietario,

        @Schema(description = "Vacunas aplicadas agrupadas por vacuna")
        List<CarnetVacunaGrupoResponse> vacunas,

        @Schema(description = "Próximos refuerzos pendientes activos")
        List<CarnetProximoRefuerzoResponse> proximosRefuerzos
) {
}
