package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarRolRequest;
import com.apiveterinaria.veterinaria.dto.CrearRolRequest;
import com.apiveterinaria.veterinaria.dto.RolResponse;
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
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Endpoints para la gestión y CRUD de roles del sistema")
public class RolController {

    private final CatalogoService catalogoService;

    public RolController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    @Operation(summary = "Listar roles del sistema")
    @ApiResponse(responseCode = "200", description = "Listado de roles obtenido exitosamente")
    public ResponseEntity<List<RolResponse>> listar() {
        return ResponseEntity.ok(catalogoService.listarRoles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un rol por su ID")
    @ApiResponse(responseCode = "200", description = "Rol obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RolResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerRolPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo rol en el sistema")
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Nombre o código de rol ya existente")
    public ResponseEntity<RolResponse> crear(@Valid @RequestBody CrearRolRequest request) {
        RolResponse respuesta = catalogoService.crearRol(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un rol")
    @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "409", description = "Ya existe otro rol con ese nombre")
    public ResponseEntity<RolResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarRolRequest request
    ) {
        return ResponseEntity.ok(catalogoService.actualizarRol(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rol")
    @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar porque existen usuarios asociados")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        catalogoService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}
