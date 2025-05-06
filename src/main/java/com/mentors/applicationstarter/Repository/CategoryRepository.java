package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String categoryName);

    List<Category> findByNameIn(Collection<String> names);
}
