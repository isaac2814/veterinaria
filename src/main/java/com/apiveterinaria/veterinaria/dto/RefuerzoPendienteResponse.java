package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.models.RefuerzoPendiente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record RefuerzoPendienteResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID registroVacunacionId,

        @Schema(example = "2026-11-26")
        LocalDate fechaProximoRefuerzo,

        @Schema(example = "21", description = "Días restantes. Negativo si ya está atrasado")
        Integer diasRestantes,

        @Schema(example = "1")
        Integer numeroDosis,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID mascotaId,

        @Schema(example = "Rocky")
        String mascota,

        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa8")
        UUID propietarioId,

        @Schema(example = "Laura Martínez")
        String propietario,

        @Schema(example = "+573001234567")
        String telefonoWhatsapp,

        @Schema(example = "true")
        Boolean consentimientoWhatsapp,

        @Schema(example = "Parvovirus")
        String vacuna
) {
    public static RefuerzoPendienteResponse from(RefuerzoPendiente r) {
        if (r == null) return null;
        return new RefuerzoPendienteResponse(
                r.getRegistroVacunacionId(),
                r.getFechaProximoRefuerzo(),
                r.getDiasRestantes(),
                r.getNumeroDosis(),
                r.getMascotaId(),
                r.getMascota(),
                r.getPropietarioId(),
                r.getPropietario(),
                r.getTelefonoWhatsapp(),
                r.getConsentimientoWhatsapp(),
                r.getVacuna()
        );
    }
}
