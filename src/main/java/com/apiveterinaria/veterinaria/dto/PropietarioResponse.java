package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.TipoDocumento;
import com.apiveterinaria.veterinaria.models.Propietario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PropietarioResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(example = "CC")
        TipoDocumento tipoDocumento,

        @Schema(example = "1020304050")
        String documento,

        @Schema(example = "Laura")
        String nombres,

        @Schema(example = "Martínez Ruiz")
        String apellidos,

        @Schema(example = "+573001234567")
        String telefonoWhatsapp,

        @Schema(example = "laura.martinez@correo.com")
        String email,

        @Schema(example = "Carrera 45 # 12-34 Apto 501, Medellín")
        String direccion,

        @Schema(example = "true")
        Boolean consentimientoWhatsapp,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime fechaConsentimiento,

        @Schema(example = "true")
        Boolean activo,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime createdAt,

        @Schema(example = "2026-10-29T19:00:00Z")
        OffsetDateTime updatedAt
) {
    public static PropietarioResponse from(Propietario p) {
        if (p == null) return null;
        return new PropietarioResponse(
                p.getId(),
                p.getTipoDocumento(),
                p.getDocumento(),
                p.getNombres(),
                p.getApellidos(),
                p.getTelefonoWhatsapp(),
                p.getEmail(),
                p.getDireccion(),
                p.getConsentimientoWhatsapp(),
                p.getFechaConsentimiento(),
                p.getActivo(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
