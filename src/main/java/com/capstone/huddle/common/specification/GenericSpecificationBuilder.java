package com.capstone.huddle.common.specification;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GenericSpecificationBuilder<T> {

    public Specification<T> build() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public Specification<T> withTextSearch(String searchTerm, String... fields) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            for (String field : fields) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(field)), likePattern
                ));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<T> withDateRange(LocalDateTime startDate, LocalDateTime endDate, String dateField) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(dateField), startDate, endDate);
        };
    }

    public Specification<T> withBoolean(Boolean value, String field) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(field), value);
        };
    }

    public Specification<T> withEquals(Object value, String field) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(field), value);
        };
    }
}
