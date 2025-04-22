package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Repository.CategoryRepository;
import com.mentors.applicationstarter.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category createdCategory) {

        Category category = Category.builder()
                .UUID(UUID.randomUUID())
                .name(createdCategory.getName().toUpperCase().trim())
                .created(Instant.now())
                .build();

        if(category.getName().isEmpty()){
            throw new RuntimeException("Category name cannot be empty string");
        } else {
            return saveCategory(category);
        }
    }

    @Override
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = findCategoryById(id).orElseThrow(() -> new RuntimeException(String.format("Category with id %d was not found!", id)));

        category.setName(updatedCategory.getName().toUpperCase().trim());
        category.setUpdated(Instant.now());

        return saveCategory(category);
    }

    @Override
    public Category deleteCategory(Long id) {
        Category category = findCategoryById(id).orElseThrow(() -> new RuntimeException(String.format("Category with id %d was not found!", id)));

        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            return null;
        }

        return category;
    }


    private Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    private Category saveCategory(Category category) {
        try {
            categoryRepository.save(category);
            return category;
        } catch (Exception e){
            return null;
        }
    }
}
