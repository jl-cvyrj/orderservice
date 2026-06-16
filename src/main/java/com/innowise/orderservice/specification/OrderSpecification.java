package com.innowise.orderservice.specification;

import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<Order> createdBetween (Instant start, Instant end) {
        return (root, query, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }
            if (start != null && end == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start);
            }
            if (start == null && end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end);
            }
            return criteriaBuilder.between(root.get("createdAt"), start, end);
        };
    }

    public static Specification<Order> hasStatus (List<OrderStatus> statuses) {
        return (root, query, criteriaBuilder) ->  {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }
}
