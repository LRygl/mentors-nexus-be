package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Exception.ResourceNotEmptyException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
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
        String categoryName = createdCategory.getName().toUpperCase().trim();

        if(categoryName.isEmpty()) {
            throw new ResourceNotEmptyException(ErrorCodes.CATEGORY_EMPTY);
        }

        if(categoryRepository.findByName(categoryName).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorCodes.CATEGORY_EXISTS);
        }

        Category category = Category.builder()
                .UUID(UUID.randomUUID())
                .name(categoryName)
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
        Category category = findCategoryById(id);

        category.setName(updatedCategory.getName().toUpperCase().trim());
        category.setUpdated(Instant.now());

        return saveCategory(category);
    }

    @Override
    public Category deleteCategory(Long id) {
        Category category = findCategoryById(id);

        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            return null;
        }

        return category;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return findCategoryById(categoryId);
    }


    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.CATEGORY_DOES_NOT_EXIST));
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
