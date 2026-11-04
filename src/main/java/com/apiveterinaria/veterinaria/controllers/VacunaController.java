package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarVacunaRequest;
import com.apiveterinaria.veterinaria.dto.CrearVacunaRequest;
import com.apiveterinaria.veterinaria.dto.VacunaResponse;
import com.apiveterinaria.veterinaria.services.VacunaService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/vacunas")
@Tag(name = "Vacunas", description = "Endpoints para la gestión del catálogo de vacunas")
public class VacunaController {

    private final VacunaService vacunaService;

    public VacunaController(VacunaService vacunaService) {
        this.vacunaService = vacunaService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva vacuna")
    @ApiResponse(responseCode = "201", description = "Vacuna creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El código de la vacuna ya existe")
    public ResponseEntity<VacunaResponse> crear(@Valid @RequestBody CrearVacunaRequest request) {
        VacunaResponse respuesta = vacunaService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar vacunas con búsqueda y paginación")
    @ApiResponse(responseCode = "200", description = "Página de vacunas obtenida exitosamente")
    public ResponseEntity<Page<VacunaResponse>> listar(
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return ResponseEntity.ok(vacunaService.listar(q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una vacuna por su ID")
    @ApiResponse(responseCode = "200", description = "Vacuna obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Vacuna no encontrada")
    public ResponseEntity<VacunaResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(vacunaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una vacuna")
    @ApiResponse(responseCode = "200", description = "Vacuna actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Vacuna no encontrada")
    @ApiResponse(responseCode = "409", description = "El código ya está en uso por otra vacuna")
    public ResponseEntity<VacunaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarVacunaRequest request
    ) {
        return ResponseEntity.ok(vacunaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una vacuna (borrado físico)")
    @ApiResponse(responseCode = "204", description = "Vacuna eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Vacuna no encontrada")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar la vacuna porque tiene esquemas o lotes asociados")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        vacunaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
