package com.apiveterinaria.veterinaria.models;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "dosis_esquema", uniqueConstraints = {
        @UniqueConstraint(name = "dosis_esquema_esquema_id_numero_dosis_key", columnNames = {"esquema_id", "numero_dosis"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class DosisEsquema {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esquema_id", nullable = false)
    private EsquemaVacunacion esquema;

    @Column(name = "numero_dosis", nullable = false)
    private Integer numeroDosis;

    @Column(name = "intervalo_cantidad")
    private Integer intervaloCantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "intervalo_unidad", length = 20)
    private UnidadTiempo intervaloUnidad;

    @Column(name = "edad_recomendada_valor")
    private Integer edadRecomendadaValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "edad_recomendada_unidad", length = 20)
    private UnidadTiempo edadRecomendadaUnidad;

    @Column(name = "es_refuerzo", nullable = false)
    private Boolean esRefuerzo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DosisEsquema that = (DosisEsquema) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
