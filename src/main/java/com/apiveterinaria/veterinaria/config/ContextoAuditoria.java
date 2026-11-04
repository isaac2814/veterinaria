package com.apiveterinaria.veterinaria.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ContextoAuditoria {

    @PersistenceContext
    private final EntityManager entityManager;

    public ContextoAuditoria(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void aplicar() {
        String usuarioId = "";
        String ipOrigen = "127.0.0.1";

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            String headerUsuario = request.getHeader("X-Usuario-Id");
            if (headerUsuario != null && !headerUsuario.isBlank()) {
                usuarioId = headerUsuario.trim();
            }

            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                ipOrigen = xForwardedFor.split(",")[0].trim();
            } else if (request.getRemoteAddr() != null && !request.getRemoteAddr().isBlank()) {
                ipOrigen = request.getRemoteAddr();
            }
        }

        entityManager.createNativeQuery(
                "SELECT set_config('app.usuario_id', :usuario, true), " +
                "       set_config('app.origen', 'API', true), " +
                "       set_config('app.ip_origen', :ip, true)"
        )
        .setParameter("usuario", usuarioId)
        .setParameter("ip", ipOrigen)
        .getSingleResult();
    }
}
