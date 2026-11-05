package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BajaVencidosResponse(
        @Schema(example = "1", description = "Cantidad de lotes dados de baja por vencimiento")
        int lotesDadosDeBaja
) {
}
