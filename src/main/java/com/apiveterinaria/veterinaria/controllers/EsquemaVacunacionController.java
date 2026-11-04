package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarEsquemaRequest;
import com.apiveterinaria.veterinaria.dto.CrearEsquemaRequest;
import com.apiveterinaria.veterinaria.dto.EsquemaDetalleResponse;
import com.apiveterinaria.veterinaria.dto.EsquemaListadoResponse;
import com.apiveterinaria.veterinaria.services.EsquemaVacunacionService;
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
@RequestMapping("/api/esquemas")
@Tag(name = "Esquemas de Vacunación", description = "Endpoints para la gestión de esquemas de vacunación")
public class EsquemaVacunacionController {

    private final EsquemaVacunacionService esquemaVacunacionService;

    public EsquemaVacunacionController(EsquemaVacunacionService esquemaVacunacionService) {
        this.esquemaVacunacionService = esquemaVacunacionService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo esquema de vacunación")
    @ApiResponse(responseCode = "201", description = "Esquema creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o par edadInicial incompleto")
    @ApiResponse(responseCode = "404", description = "Vacuna o especie no encontrada")
    @ApiResponse(responseCode = "409", description = "Ya existe un esquema con la misma vacuna, especie, nombre y versión")
    public ResponseEntity<EsquemaDetalleResponse> crear(@Valid @RequestBody CrearEsquemaRequest request) {
        EsquemaDetalleResponse respuesta = esquemaVacunacionService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar esquemas de vacunación (sin dosis) con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Página de esquemas obtenida exitosamente")
    public ResponseEntity<Page<EsquemaListadoResponse>> listar(
            @RequestParam(required = false) UUID vacunaId,
            @RequestParam(required = false) UUID especieId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(esquemaVacunacionService.listar(vacunaId, especieId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un esquema de vacunación por su ID (incluye lista de dosis)")
    @ApiResponse(responseCode = "200", description = "Esquema obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Esquema no encontrado")
    public ResponseEntity<EsquemaDetalleResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(esquemaVacunacionService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un esquema de vacunación")
    @ApiResponse(responseCode = "200", description = "Esquema actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o par edadInicial incompleto")
    @ApiResponse(responseCode = "404", description = "Esquema no encontrado")
    @ApiResponse(responseCode = "409", description = "Conflicto con otro esquema idéntico")
    public ResponseEntity<EsquemaDetalleResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEsquemaRequest request
    ) {
        return ResponseEntity.ok(esquemaVacunacionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un esquema de vacunación (borrado en cascada de sus dosis)")
    @ApiResponse(responseCode = "204", description = "Esquema eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Esquema no encontrado")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar el esquema porque alguna dosis ya fue aplicada en registros clínicos")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        esquemaVacunacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
