package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.ActualizarPropietarioRequest;
import com.apiveterinaria.veterinaria.dto.CrearPropietarioRequest;
import com.apiveterinaria.veterinaria.dto.PropietarioResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Propietario;
import com.apiveterinaria.veterinaria.repositories.PropietarioRepository;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PropietarioService {

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id", "documento", "nombres", "apellidos", "telefonoWhatsapp", "email", "activo", "createdAt", "updatedAt"
    );

    private final PropietarioRepository propietarioRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public PropietarioService(PropietarioRepository propietarioRepository,
                              ContextoAuditoria contextoAuditoria,
                              EntityManager entityManager) {
        this.propietarioRepository = propietarioRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public PropietarioResponse crear(CrearPropietarioRequest request) {
        contextoAuditoria.aplicar();

        Propietario propietario = Propietario.builder()
                .tipoDocumento(request.tipoDocumento())
                .documento(request.documento().trim())
                .nombres(request.nombres().trim())
                .apellidos(request.apellidos().trim())
                .telefonoWhatsapp(request.telefonoWhatsapp().trim())
                .email(request.email() != null && !request.email().isBlank() ? request.email().trim().toLowerCase() : null)
                .direccion(request.direccion().trim())
                .consentimientoWhatsapp(request.consentimientoWhatsapp() != null ? request.consentimientoWhatsapp() : true)
                .fechaConsentimiento(OffsetDateTime.now())
                .activo(true)
                .build();

        propietario = propietarioRepository.save(propietario);
        entityManager.flush();
        entityManager.refresh(propietario);

        return PropietarioResponse.from(propietario);
    }

    public Page<PropietarioResponse> listar(Boolean activo, String q, Pageable pageable) {
        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "createdAt");
        String filtroQ = (q != null && !q.isBlank()) ? q.trim() : null;

        return propietarioRepository.buscarPropietarios(activo, filtroQ, pageableValido)
                .map(PropietarioResponse::from);
    }

    public PropietarioResponse obtenerPorId(UUID id) {
        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con ID: " + id));
        return PropietarioResponse.from(propietario);
    }

    @Transactional
    public PropietarioResponse actualizar(UUID id, ActualizarPropietarioRequest request) {
        contextoAuditoria.aplicar();

        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con ID: " + id));

        propietario.setTipoDocumento(request.tipoDocumento());
        propietario.setDocumento(request.documento().trim());
        propietario.setNombres(request.nombres().trim());
        propietario.setApellidos(request.apellidos().trim());
        propietario.setTelefonoWhatsapp(request.telefonoWhatsapp().trim());
        propietario.setEmail(request.email() != null && !request.email().isBlank() ? request.email().trim().toLowerCase() : null);
        propietario.setDireccion(request.direccion().trim());

        propietario = propietarioRepository.save(propietario);
        entityManager.flush();
        entityManager.refresh(propietario);

        return PropietarioResponse.from(propietario);
    }

    @Transactional
    public PropietarioResponse cambiarConsentimientoWhatsapp(UUID id, boolean valor) {
        contextoAuditoria.aplicar();

        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con ID: " + id));

        propietario.setConsentimientoWhatsapp(valor);
        propietario.setFechaConsentimiento(OffsetDateTime.now());

        propietario = propietarioRepository.save(propietario);
        entityManager.flush();
        entityManager.refresh(propietario);

        return PropietarioResponse.from(propietario);
    }

    @Transactional
    public void desactivar(UUID id) {
        contextoAuditoria.aplicar();

        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con ID: " + id));

        propietario.setActivo(false);
        propietarioRepository.save(propietario);
        entityManager.flush();
    }
}
