package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import io.swagger.v3.oas.annotations.media.Schema;

public record EdadTiempoResponse(
        @Schema(example = "6")
        Integer valor,

        @Schema(example = "SEMANAS")
        UnidadTiempo unidad
) {
}
