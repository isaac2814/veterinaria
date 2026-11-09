package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CrearRazaRequest(
        @Schema(description = "ID de la especie existente (opcional si se especifica nombreEspecie)", example = "08309f6b-a555-4a24-a130-43733b37c8d6")
        UUID especieId,

        @Schema(description = "Nombre de la especie (ej. 'Canino' o 'Felino'). Si se envía y la especie existe se vincula automáticamente; si no existe, se crea.", example = "Canino")
        String nombreEspecie,

        @NotBlank(message = "El nombre de la raza es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Golden Retriever")
        String nombre,

        @Schema(example = "Raza de tamaño mediano-grande originaria del Reino Unido")
        String descripcion
) {
}
