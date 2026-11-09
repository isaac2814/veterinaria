package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarEspecieRequest;
import com.apiveterinaria.veterinaria.dto.CrearEspecieRequest;
import com.apiveterinaria.veterinaria.dto.EspecieResponse;
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
@RequestMapping("/api/especies")
@Tag(name = "Especies", description = "Endpoints para la gestión y CRUD de especies de animales")
public class EspecieController {

    private final CatalogoService catalogoService;

    public EspecieController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    @Operation(summary = "Listar especies animales registradas")
    @ApiResponse(responseCode = "200", description = "Listado de especies obtenido exitosamente")
    public ResponseEntity<List<EspecieResponse>> listar() {
        return ResponseEntity.ok(catalogoService.listarEspecies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una especie por su ID")
    @ApiResponse(responseCode = "200", description = "Especie obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    public ResponseEntity<EspecieResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerEspeciePorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva especie animal")
    @ApiResponse(responseCode = "201", description = "Especie creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Nombre o código de especie ya existente")
    public ResponseEntity<EspecieResponse> crear(@Valid @RequestBody CrearEspecieRequest request) {
        EspecieResponse respuesta = catalogoService.crearEspecie(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una especie animal")
    @ApiResponse(responseCode = "200", description = "Especie actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    @ApiResponse(responseCode = "409", description = "Ya existe otra especie con ese nombre")
    public ResponseEntity<EspecieResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEspecieRequest request
    ) {
        return ResponseEntity.ok(catalogoService.actualizarEspecie(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una especie animal")
    @ApiResponse(responseCode = "204", description = "Especie eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar porque tiene razas o mascotas asociadas")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        catalogoService.eliminarEspecie(id);
        return ResponseEntity.noContent().build();
    }
}
