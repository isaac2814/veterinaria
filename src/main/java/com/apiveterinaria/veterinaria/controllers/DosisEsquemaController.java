package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarDosisRequest;
import com.apiveterinaria.veterinaria.dto.CrearDosisRequest;
import com.apiveterinaria.veterinaria.dto.DosisResponse;
import com.apiveterinaria.veterinaria.services.DosisEsquemaService;
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
@RequestMapping("/api/esquemas/{esquemaId}/dosis")
@Tag(name = "Dosis de Esquemas", description = "Endpoints para la gestión de dosis dentro de esquemas de vacunación")
public class DosisEsquemaController {

    private final DosisEsquemaService dosisEsquemaService;

    public DosisEsquemaController(DosisEsquemaService dosisEsquemaService) {
        this.dosisEsquemaService = dosisEsquemaService;
    }

    @PostMapping
    @Operation(summary = "Agregar una nueva dosis al esquema de vacunación")
    @ApiResponse(responseCode = "201", description = "Dosis agregada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o pares intervalo/edad incompleto")
    @ApiResponse(responseCode = "404", description = "Esquema no encontrado")
    @ApiResponse(responseCode = "409", description = "El número de dosis ya existe en este esquema")
    public ResponseEntity<DosisResponse> crear(
            @PathVariable UUID esquemaId,
            @Valid @RequestBody CrearDosisRequest request
    ) {
        DosisResponse respuesta = dosisEsquemaService.crear(esquemaId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar todas las dosis de un esquema ordenadas por número de dosis")
    @ApiResponse(responseCode = "200", description = "Lista de dosis obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Esquema no encontrado")
    public ResponseEntity<List<DosisResponse>> listarPorEsquema(@PathVariable UUID esquemaId) {
        return ResponseEntity.ok(dosisEsquemaService.listarPorEsquema(esquemaId));
    }

    @GetMapping("/{dosisId}")
    @Operation(summary = "Obtener una dosis por su ID y esquema")
    @ApiResponse(responseCode = "200", description = "Dosis obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Dosis no encontrada o no pertenece al esquema indicado")
    public ResponseEntity<DosisResponse> obtenerPorId(
            @PathVariable UUID esquemaId,
            @PathVariable UUID dosisId
    ) {
        return ResponseEntity.ok(dosisEsquemaService.obtenerPorId(esquemaId, dosisId));
    }

    @PutMapping("/{dosisId}")
    @Operation(
            summary = "Actualizar datos de una dosis del esquema",
            description = "Permite editar intervalo, edad recomendada, esRefuerzo y observaciones (el número de dosis no puede cambiarse). NOTA: Cambiar un intervalo solo afecta a las aplicaciones clínicas futuras, ya que cada registro de vacunación previo conserva su fecha de refuerzo ya calculada."
    )
    @ApiResponse(responseCode = "200", description = "Dosis actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o pares intervalo/edad incompleto")
    @ApiResponse(responseCode = "404", description = "Dosis no encontrada o no pertenece al esquema indicado")
    public ResponseEntity<DosisResponse> actualizar(
            @PathVariable UUID esquemaId,
            @PathVariable UUID dosisId,
            @Valid @RequestBody ActualizarDosisRequest request
    ) {
        return ResponseEntity.ok(dosisEsquemaService.actualizar(esquemaId, dosisId, request));
    }

    @DeleteMapping("/{dosisId}")
    @Operation(summary = "Eliminar una dosis del esquema")
    @ApiResponse(responseCode = "204", description = "Dosis eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Dosis no encontrada o no pertenece al esquema indicado")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar la dosis porque ya fue aplicada en registros clínicos")
    public ResponseEntity<Void> eliminar(
            @PathVariable UUID esquemaId,
            @PathVariable UUID dosisId
    ) {
        dosisEsquemaService.eliminar(esquemaId, dosisId);
        return ResponseEntity.noContent().build();
    }
}
