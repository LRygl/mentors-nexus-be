package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO createCategory(Category category);

    Category updateCategory(Long id, Category category);

    Category deleteCategory(Long id);

    CategoryDTO getCategoryById(Long categoryId);

    Page<CategoryDTO> getPagedCategories(String name, Pageable pageable);
}
