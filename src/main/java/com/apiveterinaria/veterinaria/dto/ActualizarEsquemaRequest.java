package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ActualizarEsquemaRequest(
        @NotBlank(message = "El nombre del esquema es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        @Schema(example = "Parvovirus canino - cachorros modificado")
        String nombre,

        @PositiveOrZero(message = "El valor de la edad inicial no puede ser negativo")
        @Schema(example = "6")
        Integer edadInicialValor,

        @Schema(example = "SEMANAS")
        UnidadTiempo edadInicialUnidad,

        @Schema(example = "Observaciones actualizadas")
        String observaciones
) {
    @AssertTrue(message = "La edad inicial debe tener tanto valor como unidad, o ambos deben ser nulos")
    public boolean isEdadInicialCompleta() {
        return (edadInicialValor == null) == (edadInicialUnidad == null);
    }
}
