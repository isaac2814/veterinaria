package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarVacunaRequest(
        @NotBlank(message = "El código de la vacuna es obligatorio")
        @Size(max = 50, message = "El código no puede superar 50 caracteres")
        @Schema(example = "PARVO")
        String codigo,

        @NotBlank(message = "El nombre de la vacuna es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        @Schema(example = "Parvovirus Canino")
        String nombre,

        @Size(max = 150, message = "El fabricante no puede superar 150 caracteres")
        @Schema(example = "Zoetis")
        String fabricante,

        @Schema(example = "Vacuna preventiva actualizada")
        String descripcion
) {
}
