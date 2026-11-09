package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarMovimientoRequest;
import com.apiveterinaria.veterinaria.dto.CrearMovimientoRequest;
import com.apiveterinaria.veterinaria.dto.MovimientoResponse;
import com.apiveterinaria.veterinaria.enums.TipoMovimiento;
import com.apiveterinaria.veterinaria.services.MovimientoInventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/movimientos-inventario")
@Tag(name = "Movimientos de Inventario", description = "Endpoints para consulta y registro de movimientos de inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    @Operation(summary = "Listar movimientos de inventario con filtros y paginación")
    public ResponseEntity<Page<MovimientoResponse>> listar(
            @RequestParam(required = false) UUID loteId,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(movimientoService.listar(loteId, tipo, desde, hasta, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un movimiento de inventario por su ID")
    public ResponseEntity<MovimientoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un movimiento manual de inventario (solo AJUSTE)",
            description = "Registra un movimiento de ajuste. La cantidad puede ser positiva o negativa. Los demás tipos de movimiento son automáticos.",
            parameters = {
                    @Parameter(
                            name = "X-Usuario-Id",
                            in = ParameterIn.HEADER,
                            description = "UUID del usuario actor (opcional, para trazabilidad y auditoría)",
                            schema = @Schema(type = "string", format = "uuid", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                    )
            }
    )
    public ResponseEntity<MovimientoResponse> crear(@RequestBody @Valid CrearMovimientoRequest request) {
        MovimientoResponse response = movimientoService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar observaciones de un movimiento de inventario")
    @ApiResponse(responseCode = "200", description = "Observaciones del movimiento actualizadas")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<MovimientoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarMovimientoRequest request
    ) {
        return ResponseEntity.ok(movimientoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un movimiento de inventario")
    @ApiResponse(responseCode = "204", description = "Movimiento eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
