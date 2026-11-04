package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.Raza;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RazaResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID especieId,

        @Schema(example = "Canino")
        String especieNombre,

        @Schema(example = "Labrador Retriever")
        String nombre,

        @Schema(example = "Raza dócil, inteligente y enérgica")
        String descripcion
) {
    public static RazaResponse from(Raza raza) {
        if (raza == null) return null;
        return new RazaResponse(
                raza.getId(),
                raza.getEspecie() != null ? raza.getEspecie().getId() : null,
                raza.getEspecie() != null ? raza.getEspecie().getNombre() : null,
                raza.getNombre(),
                raza.getDescripcion()
        );
    }
}
