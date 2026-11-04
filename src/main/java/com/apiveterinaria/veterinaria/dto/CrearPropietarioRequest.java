package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.TipoDocumento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearPropietarioRequest(
        @NotNull(message = "El tipo de documento es obligatorio")
        @Schema(example = "CC")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 50, message = "El documento no puede superar 50 caracteres")
        @Schema(example = "1020304050")
        String documento,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
        @Schema(example = "Laura")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        @Schema(example = "Martínez Ruiz")
        String apellidos,

        @NotBlank(message = "El teléfono de WhatsApp es obligatorio")
        @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
        @Schema(example = "+573001234567")
        String telefonoWhatsapp,

        @Email(message = "El formato del email es inválido")
        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        @Schema(example = "laura.martinez@correo.com")
        String email,

        @NotBlank(message = "La dirección es obligatoria")
        @Schema(example = "Carrera 45 # 12-34 Apto 501, Medellín")
        String direccion,

        @Schema(example = "true")
        Boolean consentimientoWhatsapp
) {
}
