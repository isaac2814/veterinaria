package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.dto.*;
import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Mascota;
import com.apiveterinaria.veterinaria.models.RegistroVacunacion;
import com.apiveterinaria.veterinaria.models.Vacuna;
import com.apiveterinaria.veterinaria.repositories.MascotaRepository;
import com.apiveterinaria.veterinaria.repositories.RegistroVacunacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CarnetService {

    private static final ZoneId ZONA_BOGOTA = ZoneId.of("America/Bogota");

    private final MascotaRepository mascotaRepository;
    private final RegistroVacunacionRepository registroVacunacionRepository;

    public CarnetService(MascotaRepository mascotaRepository,
                         RegistroVacunacionRepository registroVacunacionRepository) {
        this.mascotaRepository = mascotaRepository;
        this.registroVacunacionRepository = registroVacunacionRepository;
    }

    @Transactional(readOnly = true)
    public CarnetVacunacionResponse obtenerCarnet(UUID mascotaId, boolean incluirAnuladas) {
        Mascota mascota = mascotaRepository.findWithDetallesById(mascotaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con ID: " + mascotaId));

        List<RegistroVacunacion> registros = registroVacunacionRepository.findParaCarnet(mascotaId, incluirAnuladas);

        CarnetMascotaResponse mascotaDto = CarnetMascotaResponse.from(mascota);
        CarnetPropietarioResponse propietarioDto = CarnetPropietarioResponse.from(mascota.getPropietario());

        // Agrupar aplicaciones por vacuna manteniendo orden de aparición
        Map<UUID, Vacuna> vacunaMap = new LinkedHashMap<>();
        Map<UUID, List<CarnetAplicacionResponse>> aplicacionesPorVacuna = new LinkedHashMap<>();

        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);
        List<CarnetProximoRefuerzoResponse> proximosRefuerzos = new ArrayList<>();

        for (RegistroVacunacion reg : registros) {
            Vacuna v = reg.getLoteVacuna() != null ? reg.getLoteVacuna().getVacuna() : null;
            if (v != null) {
                vacunaMap.putIfAbsent(v.getId(), v);
                aplicacionesPorVacuna.computeIfAbsent(v.getId(), k -> new ArrayList<>())
                        .add(CarnetAplicacionResponse.from(reg));
            }

            // Proximos refuerzos solo de registros ACTIVOS con refuerzo PENDIENTE
            if (reg.getEstadoRegistro() == EstadoRegistro.ACTIVO
                    && reg.getEstadoRefuerzo() == EstadoRefuerzo.PENDIENTE
                    && reg.getFechaProximoRefuerzo() != null) {
                String nombreVacuna = v != null ? v.getNombre() : "Vacuna";
                long dias = ChronoUnit.DAYS.between(hoy, reg.getFechaProximoRefuerzo());
                proximosRefuerzos.add(new CarnetProximoRefuerzoResponse(
                        nombreVacuna,
                        reg.getFechaProximoRefuerzo(),
                        dias
                ));
            }
        }

        List<CarnetVacunaGrupoResponse> vacunasGrupos = new ArrayList<>();
        for (Map.Entry<UUID, List<CarnetAplicacionResponse>> entry : aplicacionesPorVacuna.entrySet()) {
            Vacuna v = vacunaMap.get(entry.getKey());
            vacunasGrupos.add(new CarnetVacunaGrupoResponse(
                    VacunaResumenResponse.from(v),
                    entry.getValue()
            ));
        }

        proximosRefuerzos.sort(Comparator.comparing(CarnetProximoRefuerzoResponse::fechaProximoRefuerzo));

        return new CarnetVacunacionResponse(
                mascotaDto,
                propietarioDto,
                vacunasGrupos,
                proximosRefuerzos
        );
    }
}
