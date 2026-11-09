package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.RefuerzoPendienteResponse;
import com.apiveterinaria.veterinaria.services.RefuerzoPendienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/refuerzos")
@Tag(name = "Refuerzos", description = "Endpoints para la consulta y seguimiento de refuerzos de vacunación pendientes")
public class RefuerzoPendienteController {

    private final RefuerzoPendienteService refuerzoPendienteService;

    public RefuerzoPendienteController(RefuerzoPendienteService refuerzoPendienteService) {
        this.refuerzoPendienteService = refuerzoPendienteService;
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar refuerzos de vacunación pendientes y atrasados")
    @ApiResponse(responseCode = "200", description = "Página de refuerzos pendientes obtenida exitosamente")
    public ResponseEntity<Page<RefuerzoPendienteResponse>> listarPendientes(
            @RequestParam(required = false, defaultValue = "30") Integer dias,
            @RequestParam(required = false) UUID propietarioId,
            @RequestParam(required = false) Boolean soloAtrasados,
            Pageable pageable
    ) {
        return ResponseEntity.ok(refuerzoPendienteService.listar(dias, propietarioId, soloAtrasados, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un refuerzo pendiente por ID de registro de vacunación")
    @ApiResponse(responseCode = "200", description = "Refuerzo obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Refuerzo no encontrado")
    public ResponseEntity<RefuerzoPendienteResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(refuerzoPendienteService.obtenerPorId(id));
    }
}
