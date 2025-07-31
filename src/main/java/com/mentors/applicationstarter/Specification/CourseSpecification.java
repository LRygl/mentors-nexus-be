package com.mentors.applicationstarter.Specification;

import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Course;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class CourseSpecification {

    public static Specification<Course> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");

    }

    public static Specification<Course> hasCategories(Set<String> categoryNames) {
        return (root, query, criteriaBuilder) -> {
            if (categoryNames == null || categoryNames.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            // Join to categories
            Join<Course, Category> categoriesJoin = root.join("categories", JoinType.INNER);
            return categoriesJoin.get("name").in(categoryNames);
        };
    }

}
