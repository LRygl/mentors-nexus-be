package com.mentors.applicationstarter.Specification;

import com.mentors.applicationstarter.Model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> hasName(String name) {
        return ( root, query, criteriaBuilder ) ->
            name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%" );
    }
}
