package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CrearEsquemaRequest(
        @NotNull(message = "El ID de la vacuna es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID vacunaId,

        @NotNull(message = "El ID de la especie es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID especieId,

        @NotBlank(message = "El nombre del esquema es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        @Schema(example = "Parvovirus canino - cachorros")
        String nombre,

        @PositiveOrZero(message = "El valor de la edad inicial no puede ser negativo")
        @Schema(example = "6")
        Integer edadInicialValor,

        @Schema(example = "SEMANAS")
        UnidadTiempo edadInicialUnidad,

        @Schema(example = "Esquema básico para cachorros")
        String observaciones
) {
    @AssertTrue(message = "La edad inicial debe tener tanto valor como unidad, o ambos deben ser nulos")
    public boolean isEdadInicialCompleta() {
        return (edadInicialValor == null) == (edadInicialUnidad == null);
    }
}
