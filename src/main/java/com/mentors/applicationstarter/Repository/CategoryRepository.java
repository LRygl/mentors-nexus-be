package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByName(String categoryName);

    List<Category> findByNameIn(Collection<String> names);

    @EntityGraph(attributePaths = "courses")
    Category findWithCoursesById(Long id);
}
