package com.apiveterinaria.veterinaria.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseViewInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseViewInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseViewInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Verificando existencia de la vista v_refuerzos_pendientes...");
            jdbcTemplate.execute("""
                CREATE OR REPLACE VIEW v_refuerzos_pendientes AS
                SELECT 
                    r.id AS registro_vacunacion_id,
                    r.fecha_proximo_refuerzo,
                    (r.fecha_proximo_refuerzo - CURRENT_DATE) AS dias_restantes,
                    r.numero_dosis,
                    m.id AS mascota_id,
                    m.nombre AS mascota,
                    p.id AS propietario_id,
                    (p.nombres || ' ' || p.apellidos) AS propietario,
                    p.telefono_whatsapp,
                    p.consentimiento_whatsapp,
                    v.nombre AS vacuna
                FROM registros_vacunacion r
                JOIN mascotas m ON m.id = r.mascota_id
                JOIN propietarios p ON p.id = m.propietario_id
                JOIN lotes_vacuna l ON l.id = r.lote_vacuna_id
                JOIN vacunas v ON v.id = l.vacuna_id
                WHERE r.estado_registro = 'ACTIVO'
                  AND r.fecha_proximo_refuerzo IS NOT NULL;
            """);
            log.info("Vista v_refuerzos_pendientes verificada exitosamente.");
        } catch (Exception e) {
            log.warn("No se pudo crear/verificar la vista v_refuerzos_pendientes: {}", e.getMessage());
        }
    }
}
