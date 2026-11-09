package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarRazaRequest;
import com.apiveterinaria.veterinaria.dto.CrearRazaRequest;
import com.apiveterinaria.veterinaria.dto.RazaResponse;
import com.apiveterinaria.veterinaria.services.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/razas")
@Tag(name = "Razas", description = "Endpoints para la gestión y CRUD de razas de mascotas")
public class RazaController {

    private final CatalogoService catalogoService;

    public RazaController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    @Operation(summary = "Listar razas, opcionalmente filtradas por ID de especie")
    @ApiResponse(responseCode = "200", description = "Listado de razas obtenido exitosamente")
    public ResponseEntity<List<RazaResponse>> listar(@RequestParam(required = false) UUID especieId) {
        return ResponseEntity.ok(catalogoService.listarRazas(especieId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una raza por su ID")
    @ApiResponse(responseCode = "200", description = "Raza obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Raza no encontrada")
    public ResponseEntity<RazaResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerRazaPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva raza para una especie")
    @ApiResponse(responseCode = "201", description = "Raza creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    @ApiResponse(responseCode = "409", description = "Raza ya existente para la especie indicada")
    public ResponseEntity<RazaResponse> crear(@Valid @RequestBody CrearRazaRequest request) {
        RazaResponse respuesta = catalogoService.crearRaza(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una raza")
    @ApiResponse(responseCode = "200", description = "Raza actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Raza o especie no encontrada")
    @ApiResponse(responseCode = "409", description = "Ya existe otra raza con ese nombre para la especie")
    public ResponseEntity<RazaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarRazaRequest request
    ) {
        return ResponseEntity.ok(catalogoService.actualizarRaza(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una raza")
    @ApiResponse(responseCode = "204", description = "Raza eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Raza no encontrada")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar porque tiene mascotas asociadas")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        catalogoService.eliminarRaza(id);
        return ResponseEntity.noContent().build();
    }
}
