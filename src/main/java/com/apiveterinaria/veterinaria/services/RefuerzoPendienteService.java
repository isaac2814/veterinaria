package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.dto.RefuerzoPendienteResponse;
import com.apiveterinaria.veterinaria.repositories.RefuerzoPendienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RefuerzoPendienteService {

    private final RefuerzoPendienteRepository refuerzoPendienteRepository;

    public RefuerzoPendienteService(RefuerzoPendienteRepository refuerzoPendienteRepository) {
        this.refuerzoPendienteRepository = refuerzoPendienteRepository;
    }

    @Transactional(readOnly = true)
    public Page<RefuerzoPendienteResponse> listar(
            Integer dias,
            UUID propietarioId,
            Boolean soloAtrasados,
            Pageable pageable
    ) {
        int diasEfectivos = (dias != null) ? dias : 30;

        Pageable pageableFinal;
        if (pageable == null || pageable.getSort().isUnsorted()) {
            int page = pageable != null ? pageable.getPageNumber() : 0;
            int size = pageable != null ? pageable.getPageSize() : 20;
            pageableFinal = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fechaProximoRefuerzo"));
        } else {
            pageableFinal = pageable;
        }

        return refuerzoPendienteRepository.findRefuerzos(diasEfectivos, propietarioId, soloAtrasados, pageableFinal)
                .map(RefuerzoPendienteResponse::from);
    }
}
