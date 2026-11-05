package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarObservacionesRequest;
import com.apiveterinaria.veterinaria.dto.AnularRegistroRequest;
import com.apiveterinaria.veterinaria.dto.CrearRegistroVacunacionRequest;
import com.apiveterinaria.veterinaria.dto.RegistroVacunacionDetalleResponse;
import com.apiveterinaria.veterinaria.dto.RegistroVacunacionResponse;
import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.services.RegistroVacunacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacunaciones")
@Tag(name = "Vacunaciones", description = "Endpoints para la gestión del flujo clínico de vacunación y anulación")
public class RegistroVacunacionController {

    private final RegistroVacunacionService registroVacunacionService;

    public RegistroVacunacionController(RegistroVacunacionService registroVacunacionService) {
        this.registroVacunacionService = registroVacunacionService;
    }

    @PostMapping
    @Operation(summary = "Registrar la aplicación de una vacuna a una mascota")
    @ApiResponse(responseCode = "201", description = "Vacunación registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o par de intervalo incompleto")
    @ApiResponse(responseCode = "404", description = "Mascota, lote, veterinario o dosis de esquema no encontrada")
    @ApiResponse(responseCode = "409", description = "Esa dosis ya fue aplicada a la mascota")
    @ApiResponse(responseCode = "422", description = "Error de regla de negocio clínica (lote sin stock, lote vencido, veterinario sin rol, etc.)")
    public ResponseEntity<RegistroVacunacionResponse> crear(@Valid @RequestBody CrearRegistroVacunacionRequest request) {
        RegistroVacunacionResponse respuesta = registroVacunacionService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar registros de vacunación con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Página de registros de vacunación obtenida exitosamente")
    public ResponseEntity<Page<RegistroVacunacionResponse>> listar(
            @RequestParam(required = false) UUID mascotaId,
            @RequestParam(required = false) UUID vacunaId,
            @RequestParam(required = false) UUID veterinarioId,
            @RequestParam(required = false) EstadoRegistro estado,
            @RequestParam(required = false) EstadoRefuerzo estadoRefuerzo,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(registroVacunacionService.listar(
                mascotaId, vacunaId, veterinarioId, estado, estadoRefuerzo, desde, hasta, pageable
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle clínico completo de un registro de vacunación")
    @ApiResponse(responseCode = "200", description = "Detalle del registro de vacunación obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro de vacunación no encontrado")
    public ResponseEntity<RegistroVacunacionDetalleResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(registroVacunacionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar únicamente las observaciones de un registro de vacunación")
    @ApiResponse(responseCode = "200", description = "Observaciones actualizadas exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro de vacunación no encontrado")
    public ResponseEntity<RegistroVacunacionResponse> actualizarObservaciones(
            @PathVariable UUID id,
            @RequestBody ActualizarObservacionesRequest request
    ) {
        return ResponseEntity.ok(registroVacunacionService.actualizarObservaciones(id, request));
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular un registro de vacunación activo y revertir stock y recordatorios")
    @ApiResponse(responseCode = "200", description = "Registro de vacunación anulado exitosamente")
    @ApiResponse(responseCode = "400", description = "Motivo de anulación inválido o menor a 5 caracteres")
    @ApiResponse(responseCode = "404", description = "Registro de vacunación o usuario no encontrado")
    @ApiResponse(responseCode = "409", description = "El registro ya se encuentra anulado")
    @ApiResponse(responseCode = "422", description = "Error de regla al revertir la operación")
    public ResponseEntity<RegistroVacunacionResponse> anular(
            @PathVariable UUID id,
            @Valid @RequestBody AnularRegistroRequest request
    ) {
        return ResponseEntity.ok(registroVacunacionService.anular(id, request));
    }
}
