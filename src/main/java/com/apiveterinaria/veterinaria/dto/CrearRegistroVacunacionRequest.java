package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record CrearRegistroVacunacionRequest(
        @NotNull(message = "El ID de la mascota es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID mascotaId,

        @NotNull(message = "El ID del lote de vacuna es obligatorio")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID loteVacunaId,

        @NotNull(message = "El ID del veterinario es obligatorio")
        @Schema(example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID veterinarioId,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa8", description = "Opcional. ID de la dosis de esquema aplicada")
        UUID dosisEsquemaId,

        @Schema(example = "2026-11-05", description = "Opcional. Si es null la BD usa la fecha de hoy en Colombia")
        LocalDate fechaAplicacion,

        @Schema(example = "Paciente tranquilo, tolera bien la vacunación")
        String observaciones,

        @Schema(example = "null", description = "Opcional. ID de la dosis previa si se fuerza manualmente")
        UUID dosisPreviaId,

        @Schema(example = "2026-11-26", description = "Opcional. Fecha de próximo refuerzo si se fuerza manualmente")
        LocalDate fechaProximoRefuerzo,

        @Positive(message = "El valor del intervalo de refuerzo debe ser mayor a 0")
        @Schema(example = "21", description = "Opcional. Cantidad del intervalo si se fuerza manualmente")
        Integer intervaloRefuerzoValor,

        @Schema(example = "DIAS", description = "Opcional. Unidad del intervalo (DIAS, SEMANAS, MESES, ANIOS)")
        UnidadTiempo intervaloRefuerzoUnidad
) {
    @AssertTrue(message = "El par de intervalo de refuerzo (valor y unidad) debe enviarse completo o ambos vacíos")
    @Schema(hidden = true)
    public boolean isIntervaloValido() {
        return (intervaloRefuerzoValor == null) == (intervaloRefuerzoUnidad == null);
    }
}
