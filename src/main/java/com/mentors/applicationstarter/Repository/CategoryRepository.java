package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
