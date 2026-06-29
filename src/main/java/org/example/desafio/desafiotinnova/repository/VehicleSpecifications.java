package org.example.desafio.desafiotinnova.repository;


import jakarta.persistence.criteria.Predicate;
import org.example.desafio.desafiotinnova.model.Vehicle;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VehicleSpecifications {

    //Method used to create dinamic query using some optional filters
    public static Specification<Vehicle> byFilters(
            String brand,
            Integer year,
            String color,
            BigDecimal minPriceUSD,
            BigDecimal maxPriceUSD) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por brand (Case-Insensitive)
            if (brand != null && !brand.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand")),
                        brand.toLowerCase().trim()
                ));
            }

            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), year));
            }

            if (color != null && !color.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("color")),
                        color.toLowerCase().trim()
                ));
            }

            if (minPriceUSD != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPriceUSD));
            }

            if (maxPriceUSD != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPriceUSD));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
