package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ActualizarDosisRequest(
        @Positive(message = "La cantidad del intervalo debe ser mayor a 0")
        @Schema(example = "3")
        Integer intervaloCantidad,

        @Schema(example = "SEMANAS")
        UnidadTiempo intervaloUnidad,

        @PositiveOrZero(message = "El valor de la edad recomendada no puede ser negativo")
        @Schema(example = "6")
        Integer edadRecomendadaValor,

        @Schema(example = "SEMANAS")
        UnidadTiempo edadRecomendadaUnidad,

        @Schema(example = "false")
        Boolean esRefuerzo,

        @Schema(example = "Observaciones actualizadas")
        String observaciones
) {
    @AssertTrue(message = "El intervalo debe tener tanto cantidad como unidad, o ambos deben ser nulos")
    public boolean isIntervaloCompleto() {
        return (intervaloCantidad == null) == (intervaloUnidad == null);
    }

    @AssertTrue(message = "La edad recomendada debe tener tanto valor como unidad, o ambos deben ser nulos")
    public boolean isEdadRecomendadaCompleta() {
        return (edadRecomendadaValor == null) == (edadRecomendadaUnidad == null);
    }
}
