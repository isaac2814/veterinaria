package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActualizarObservacionesRequest(
        @Schema(example = "Observación clínica actualizada tras control posterior.")
        String observaciones
) {
}
