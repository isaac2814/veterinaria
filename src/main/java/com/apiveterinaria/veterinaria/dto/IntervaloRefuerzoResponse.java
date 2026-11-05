package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import io.swagger.v3.oas.annotations.media.Schema;

public record IntervaloRefuerzoResponse(
        @Schema(example = "21")
        Integer valor,

        @Schema(example = "DIAS")
        UnidadTiempo unidad
) {
}
