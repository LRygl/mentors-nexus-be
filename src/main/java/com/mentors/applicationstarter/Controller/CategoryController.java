package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping
    public Page<CategoryDTO> getPagedCategories(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page,size,Sort.by(direction, sort[0]));
        return categoryService.getPagedCategories(name,pageable);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(),HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody Category category) {

        CategoryDTO result = categoryService.createCategory(category);
        ResponseEntity<CategoryDTO> response = new ResponseEntity<>(result, HttpStatus.CREATED);

        // Debug logging
        System.out.println("=== CONTROLLER DEBUG ===");
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Status Value: " + response.getStatusCodeValue());
        System.out.println("Response Body: " + response.getBody());

        return response;

        //return new ResponseEntity<>(categoryService.createCategory(category),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {

        return new ResponseEntity<>(categoryService.updateCategory(id,category),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
