package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarLoteRequest;
import com.apiveterinaria.veterinaria.dto.CrearLoteRequest;
import com.apiveterinaria.veterinaria.dto.LoteResponse;
import com.apiveterinaria.veterinaria.dto.MovimientoResponse;
import com.apiveterinaria.veterinaria.services.LoteVacunaService;
import com.apiveterinaria.veterinaria.services.MovimientoInventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lotes")
@Tag(name = "Lotes de Vacunas", description = "Endpoints para la gestión de lotes de vacunas e inventario")
public class LoteVacunaController {

    private final LoteVacunaService loteService;
    private final MovimientoInventarioService movimientoService;

    public LoteVacunaController(LoteVacunaService loteService,
                                MovimientoInventarioService movimientoService) {
        this.loteService = loteService;
        this.movimientoService = movimientoService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo lote de vacuna",
            description = "Crea un lote e inicializa el movimiento INGRESO automáticamente en la base de datos.",
            parameters = {
                    @Parameter(
                            name = "X-Usuario-Id",
                            in = ParameterIn.HEADER,
                            description = "UUID del usuario actor (opcional, para trazabilidad y auditoría)",
                            schema = @Schema(type = "string", format = "uuid", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                    )
            }
    )
    public ResponseEntity<LoteResponse> crear(@RequestBody @Valid CrearLoteRequest request) {
        LoteResponse response = loteService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar lotes de vacunas con filtros y paginación")
    public ResponseEntity<Page<LoteResponse>> listar(
            @RequestParam(required = false) UUID vacunaId,
            @RequestParam(required = false) Boolean soloConStock,
            @RequestParam(required = false) Boolean vencidos,
            Pageable pageable
    ) {
        return ResponseEntity.ok(loteService.listar(vacunaId, soloConStock, vencidos, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un lote de vacuna por su ID")
    public ResponseEntity<LoteResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(loteService.obtenerPorId(id));
    }

    @GetMapping("/por-vencer")
    @Operation(
            summary = "Listar lotes con stock próximos a vencer",
            description = "Devuelve lotes con stock disponible cuyo vencimiento cae entre hoy y hoy + N días (por defecto 30 días)."
    )
    public ResponseEntity<List<LoteResponse>> listarPorVencer(
            @RequestParam(defaultValue = "30") int dias
    ) {
        return ResponseEntity.ok(loteService.listarPorVencer(dias));
    }

    @GetMapping("/{id}/movimientos")
    @Operation(summary = "Listar movimientos de inventario de un lote específico")
    public ResponseEntity<Page<MovimientoResponse>> listarMovimientos(
            @PathVariable UUID id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(movimientoService.listarPorLote(id, pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar datos permitidos de un lote",
            description = "Permite actualizar numeroLote, fechaVencimiento y unidadInventario. No permite alterar vacunaId ni cantidadRecibida.",
            parameters = {
                    @Parameter(
                            name = "X-Usuario-Id",
                            in = ParameterIn.HEADER,
                            description = "UUID del usuario actor (opcional, para trazabilidad y auditoría)",
                            schema = @Schema(type = "string", format = "uuid", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                    )
            }
    )
    public ResponseEntity<LoteResponse> actualizar(
            @PathVariable UUID id,
            @RequestBody @Valid ActualizarLoteRequest request
    ) {
        return ResponseEntity.ok(loteService.actualizar(id, request));
    }
}
