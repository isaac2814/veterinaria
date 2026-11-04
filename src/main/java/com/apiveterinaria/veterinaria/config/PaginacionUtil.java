package com.apiveterinaria.veterinaria.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PaginacionUtil {

    private PaginacionUtil() {
    }

    public static Pageable validarOrden(Pageable pageable, Set<String> camposPermitidos, String campoPorDefecto) {
        if (pageable == null) {
            return PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, campoPorDefecto));
        }

        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, campoPorDefecto));
        }

        List<Sort.Order> ordenesValidos = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            if (camposPermitidos.contains(order.getProperty())) {
                ordenesValidos.add(order);
            }
        }

        Sort sortFinal = ordenesValidos.isEmpty()
                ? Sort.by(Sort.Direction.DESC, campoPorDefecto)
                : Sort.by(ordenesValidos);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortFinal);
    }
}
