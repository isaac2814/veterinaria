package com.apiveterinaria.veterinaria.models;

import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "registros_vacunacion")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroVacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false, updatable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_vacuna_id", nullable = false, updatable = false)
    private LoteVacuna loteVacuna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false, updatable = false)
    private Usuario veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosis_esquema_id", updatable = false)
    private DosisEsquema dosisEsquema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosis_previa_id", updatable = false)
    private RegistroVacunacion dosisPrevia;

    @Column(name = "numero_dosis", insertable = false, updatable = false)
    private Integer numeroDosis;

    @Column(name = "fecha_aplicacion", updatable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "fecha_proximo_refuerzo", updatable = false)
    private LocalDate fechaProximoRefuerzo;

    @Column(name = "intervalo_refuerzo_valor", updatable = false)
    private Integer intervaloRefuerzoValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "intervalo_refuerzo_unidad", length = 20, updatable = false)
    private UnidadTiempo intervaloRefuerzoUnidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_registro", length = 20, insertable = false, updatable = false)
    private EstadoRegistro estadoRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_refuerzo", length = 20, insertable = false, updatable = false)
    private EstadoRefuerzo estadoRefuerzo;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "motivo_anulacion", insertable = false, updatable = false)
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anulado_por", insertable = false, updatable = false)
    private Usuario anuladoPor;

    @Column(name = "fecha_anulacion", insertable = false, updatable = false)
    private OffsetDateTime fechaAnulacion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroVacunacion that = (RegistroVacunacion) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
