package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.BajaVencidosRequest;
import com.apiveterinaria.veterinaria.dto.BajaVencidosResponse;
import com.apiveterinaria.veterinaria.services.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Operaciones especiales de gestión de inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping("/baja-vencidos")
    @Operation(
            summary = "Dar de baja lotes vencidos con stock disponible",
            description = "Ejecuta la función de base de datos para generar los movimientos de baja de todos los lotes vencidos con stock. Es idempotente en el mismo día.",
            parameters = {
                    @Parameter(
                            name = "X-Usuario-Id",
                            in = ParameterIn.HEADER,
                            description = "UUID del usuario actor (opcional, para trazabilidad y auditoría)",
                            schema = @Schema(type = "string", format = "uuid", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                    )
            }
    )
    public ResponseEntity<BajaVencidosResponse> darBajaVencidos(@RequestBody @Valid BajaVencidosRequest request) {
        return ResponseEntity.ok(inventarioService.darBajaLotesVencidos(request));
    }
}
