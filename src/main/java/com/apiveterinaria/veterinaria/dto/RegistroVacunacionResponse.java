package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.models.RegistroVacunacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RegistroVacunacionResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        MascotaResumenResponse mascota,

        PropietarioSimpleResponse propietario,

        VacunaResumenResponse vacuna,

        LoteStockResponse lote,

        UsuarioResumenResponse veterinario,

        DosisEsquemaInfoResponse dosisEsquema,

        @Schema(example = "null")
        UUID dosisPreviaId,

        @Schema(example = "1")
        Integer numeroDosis,

        @Schema(example = "2026-11-05")
        LocalDate fechaAplicacion,

        @Schema(example = "2026-11-26")
        LocalDate fechaProximoRefuerzo,

        IntervaloRefuerzoResponse intervaloRefuerzo,

        @Schema(example = "ACTIVO")
        EstadoRegistro estadoRegistro,

        @Schema(example = "PENDIENTE")
        EstadoRefuerzo estadoRefuerzo,

        @Schema(example = "Paciente tranquilo, tolera bien la vacunación")
        String observaciones,

        AnulacionResponse anulacion,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-11-05T12:00:00Z")
        OffsetDateTime updatedAt
) {
    public static RegistroVacunacionResponse from(RegistroVacunacion r) {
        if (r == null) return null;

        MascotaResumenResponse mascota = MascotaResumenResponse.from(r.getMascota());
        PropietarioSimpleResponse propietario = (r.getMascota() != null && r.getMascota().getPropietario() != null)
                ? PropietarioSimpleResponse.from(r.getMascota().getPropietario())
                : null;

        VacunaResumenResponse vacuna = (r.getLoteVacuna() != null && r.getLoteVacuna().getVacuna() != null)
                ? VacunaResumenResponse.from(r.getLoteVacuna().getVacuna())
                : null;

        LoteStockResponse lote = LoteStockResponse.from(r.getLoteVacuna());
        UsuarioResumenResponse vet = UsuarioResumenResponse.from(r.getVeterinario());
        DosisEsquemaInfoResponse dosis = DosisEsquemaInfoResponse.from(r.getDosisEsquema());

        UUID previaId = r.getDosisPrevia() != null ? r.getDosisPrevia().getId() : null;

        IntervaloRefuerzoResponse intervalo = (r.getIntervaloRefuerzoValor() != null && r.getIntervaloRefuerzoUnidad() != null)
                ? new IntervaloRefuerzoResponse(r.getIntervaloRefuerzoValor(), r.getIntervaloRefuerzoUnidad())
                : null;

        AnulacionResponse anulacion = null;
        if (r.getEstadoRegistro() == EstadoRegistro.ANULADO) {
            anulacion = new AnulacionResponse(
                    r.getMotivoAnulacion(),
                    UsuarioResumenResponse.from(r.getAnuladoPor()),
                    r.getFechaAnulacion()
            );
        }

        return new RegistroVacunacionResponse(
                r.getId(),
                mascota,
                propietario,
                vacuna,
                lote,
                vet,
                dosis,
                previaId,
                r.getNumeroDosis(),
                r.getFechaAplicacion(),
                r.getFechaProximoRefuerzo(),
                intervalo,
                r.getEstadoRegistro(),
                r.getEstadoRefuerzo(),
                r.getObservaciones(),
                anulacion,
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
