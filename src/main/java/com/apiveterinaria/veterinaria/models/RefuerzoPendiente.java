package com.apiveterinaria.veterinaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "v_refuerzos_pendientes")
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefuerzoPendiente {

    @Id
    @Column(name = "registro_vacunacion_id")
    private UUID registroVacunacionId;

    @Column(name = "fecha_proximo_refuerzo")
    private LocalDate fechaProximoRefuerzo;

    @Column(name = "dias_restantes")
    private Integer diasRestantes;

    @Column(name = "numero_dosis")
    private Integer numeroDosis;

    @Column(name = "mascota_id")
    private UUID mascotaId;

    @Column(name = "mascota")
    private String mascota;

    @Column(name = "propietario_id")
    private UUID propietarioId;

    @Column(name = "propietario")
    private String propietario;

    @Column(name = "telefono_whatsapp")
    private String telefonoWhatsapp;

    @Column(name = "consentimiento_whatsapp")
    private Boolean consentimientoWhatsapp;

    @Column(name = "vacuna")
    private String vacuna;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefuerzoPendiente that = (RefuerzoPendiente) o;
        return registroVacunacionId != null && Objects.equals(registroVacunacionId, that.registroVacunacionId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
