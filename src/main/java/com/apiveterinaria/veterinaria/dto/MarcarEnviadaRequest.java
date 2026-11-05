package com.apiveterinaria.veterinaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MarcarEnviadaRequest(
        @Schema(example = "wamid.HBgLM2...", description = "Opcional. Identificador devuelto por el proveedor de mensajería")
        String idMensajeProveedor
) {
}
