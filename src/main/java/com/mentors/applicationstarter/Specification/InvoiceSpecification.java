package com.mentors.applicationstarter.Specification;

import com.mentors.applicationstarter.Model.Invoice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InvoiceSpecification {

    public static Specification<Invoice> withFilters(
            String invoiceNumber,
            Float minTotalAmount,
            Float maxTotalAmount,
            Instant issuedFrom,
            Instant issuedTo,
            Boolean paid
    )  {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (invoiceNumber != null && !invoiceNumber.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("invoiceNumber")), "%" + invoiceNumber.toLowerCase() + "%"));
            }

            if (minTotalAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalAmount"), minTotalAmount));
            }

            if (maxTotalAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalAmount"), maxTotalAmount));
            }

            if (issuedFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issuedDate"), issuedFrom));
            }

            if (issuedTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issuedDate"), issuedTo));
            }

            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

}
