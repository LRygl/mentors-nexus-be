package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Exception.ResourceNotEmptyException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.CategoryMapper;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Repository.CategoryRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Service.CategoryService;
import com.mentors.applicationstarter.Specification.CategorySpecification;
import com.mentors.applicationstarter.Utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Override
    public Page<CategoryDTO> getPagedCategories(String name, Pageable pageable) {
        Specification<Category> specification = CategorySpecification.hasName(name);
        Page<Category> categoryPage = categoryRepository.findAll(specification, pageable);
        return categoryPage.map(CategoryMapper::toCategoryWithCoursesDto);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toCategoryWithCoursesDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findWithCoursesById(categoryId);
        return CategoryMapper.toCategoryWithCoursesDto(category);
    }

    @Override
    public CategoryDTO createCategory(Category createdCategory) {
        String categoryName = createdCategory.getName().toUpperCase().trim();

        UUID courseCategoryUUID = UUID.randomUUID();
        UUID authenticatedUserUuid = AuthUtils.getAuthenticatedUserUuid();

        if(categoryName.isEmpty()) {
            throw new ResourceNotEmptyException(ErrorCodes.CATEGORY_EMPTY);
        }

        if(categoryRepository.findByName(categoryName).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorCodes.CATEGORY_EXISTS);
        }

        Category category = Category.builder()
                .uuid(courseCategoryUUID)
                .name(categoryName)
                .createdAt(Instant.now())
                .createdBy(authenticatedUserUuid)
                .description(createdCategory.getDescription())
                .color(createdCategory.getColor())
                .build();

        if(category.getName().isEmpty()){
            throw new RuntimeException("Category name cannot be empty string");
        } else {
            saveCategory(category);
            return CategoryMapper.toCategoryWithCoursesDto(category);
        }
    }

    @Override
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = findCategoryById(id);

        UUID authenticatedUserUuid = AuthUtils.getAuthenticatedUserUuid();

        category.setUpdatedBy(authenticatedUserUuid);
        category.setUpdatedAt(Instant.now());
        category.setName(updatedCategory.getName().toUpperCase().trim());
        category.setDescription(updatedCategory.getDescription());
        category.setColor(updatedCategory.getColor());

        return saveCategory(category);
    }

    @Override
    public Category deleteCategory(Long id) {
        Category category = findCategoryById(id);

        List<Course> courses = courseRepository.findAllByCategoriesContaining(category);

        for (Course course : courses) {
            course.getCategories().remove(category);
        }

        categoryRepository.delete(category);
        return category;
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
