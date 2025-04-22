package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<Category> getAllCategories();

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    Category deleteCategory(Long id);
}
