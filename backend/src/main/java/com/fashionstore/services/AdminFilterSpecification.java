package com.fashionstore.services;

import com.fashionstore.exceptions.ValidationException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class AdminFilterSpecification {
    private AdminFilterSpecification() {}

    public static <T> Specification<T> create(
            Map<String, Function<Root<T>, Expression<?>>> fields,
            String search,
            String filterColumn,
            String filterValue) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(search)) {
                String value = contains(search);
                predicates.add(criteriaBuilder.or(fields.values().stream()
                        .map(field -> criteriaBuilder.like(criteriaBuilder.lower(field.apply(root).as(String.class)), value))
                        .toArray(Predicate[]::new)));
            }

            if (hasText(filterValue)) {
                if (hasText(filterColumn)) {
                    Function<Root<T>, Expression<?>> field = fields.get(filterColumn);
                    if (field == null) {
                        throw new ValidationException("Unsupported filter column: " + filterColumn);
                    }
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(field.apply(root).as(String.class)),
                            contains(filterValue)));
                } else {
                    String value = contains(filterValue);
                    predicates.add(criteriaBuilder.or(fields.values().stream()
                            .map(field -> criteriaBuilder.like(criteriaBuilder.lower(field.apply(root).as(String.class)), value))
                            .toArray(Predicate[]::new)));
                }
            } else if (hasText(filterColumn) && !fields.containsKey(filterColumn)) {
                throw new ValidationException("Unsupported filter column: " + filterColumn);
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static boolean hasFilters(String search, String filterColumn, String filterValue) {
        return hasText(search) || hasText(filterColumn) || hasText(filterValue);
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String contains(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
