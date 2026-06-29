package com.fashionstore.services;

import com.fashionstore.exceptions.ValidationException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFilterSpecification {
    private AdminFilterSpecification() {}

    public static <T> Specification<T> create(
            Map<String, String> fields,
            String search,
            String filterColumn,
            String filterValue) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Map<String, From<?, ?>> joins = new HashMap<>();
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(search)) {
                String value = contains(search);
                predicates.add(criteriaBuilder.or(fields.values().stream()
                        .map(field -> criteriaBuilder.like(criteriaBuilder.lower(resolve(root, joins, field).as(String.class)), value))
                        .toArray(Predicate[]::new)));
            }

            if (hasText(filterValue)) {
                if (hasText(filterColumn)) {
                    String field = fields.get(filterColumn);
                    if (field == null) {
                        throw new ValidationException("Unsupported filter column: " + filterColumn);
                    }
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(resolve(root, joins, field).as(String.class)),
                            contains(filterValue)));
                } else {
                    String value = contains(filterValue);
                    predicates.add(criteriaBuilder.or(fields.values().stream()
                            .map(field -> criteriaBuilder.like(criteriaBuilder.lower(resolve(root, joins, field).as(String.class)), value))
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

    private static <T> Expression<?> resolve(Root<T> root, Map<String, From<?, ?>> joins, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        From<?, ?> from = root;
        String joinPath = "";

        for (int i = 0; i < parts.length - 1; i++) {
            joinPath = joinPath.isEmpty() ? parts[i] : joinPath + "." + parts[i];
            From<?, ?> join = joins.get(joinPath);
            if (join == null) {
                join = from.join(parts[i], JoinType.LEFT);
                joins.put(joinPath, join);
            }
            from = join;
        }

        return from.get(parts[parts.length - 1]);
    }
}
