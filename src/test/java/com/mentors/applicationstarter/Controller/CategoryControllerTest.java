package com.mentors.applicationstarter.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("Category Controller Unit Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryDTO categoryDTO1;
    private CategoryDTO categoryDTO2;
    private Category category;

    @BeforeEach
    void setUp() {
        // Setup test data
        categoryDTO1 = CategoryDTO.builder()
                .id(1L)
                .name("Programming")
                .description("Programming courses")
                .build();

        categoryDTO2 = CategoryDTO.builder()
                .id(2L)
                .name("Design")
                .description("Design courses")
                .build();

        category = Category.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Programming")
                .description("Programming courses")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/category - Get Paged Categories")
    void testGetPagedCategories_Success() throws Exception {
        // Given
        List<CategoryDTO> categories = Arrays.asList(categoryDTO1, categoryDTO2);
        Page<CategoryDTO> categoryPage = new PageImpl<>(categories,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")),
                categories.size());

        when(categoryService.getPagedCategories(isNull(), any(Pageable.class)))
                .thenReturn(categoryPage);

        // When & Then
        mockMvc.perform(get("/api/v1/category")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Programming")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("Design")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));

        verify(categoryService, times(1)).getPagedCategories(isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/category/all - Get All Categories")
    void testGetAllCategories_Success() throws Exception {
        // Given
        List<CategoryDTO> categories = Arrays.asList(categoryDTO1, categoryDTO2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/v1/category/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Programming")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Design")));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/v1/category/{id} - Get Category By Id")
    void testGetCategoryById_Success() throws Exception {
        // Given
        when(categoryService.getCategoryById(1L)).thenReturn(categoryDTO1);

        // When & Then
        mockMvc.perform(get("/api/v1/category/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Programming")))
                .andExpect(jsonPath("$.description", is("Programming courses")));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("POST /api/v1/category - Create Category")
    void testCreateCategory_Success() throws Exception {
        // Given
        when(categoryService.createCategory(any(Category.class)))
                .thenReturn(categoryDTO1);

        // When & Then
        mockMvc.perform(post("/api/v1/category")
                        .with(csrf())  // Add CSRF token for POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Programming")))
                .andExpect(jsonPath("$.description", is("Programming courses")));

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    @DisplayName("PUT /api/v1/category/{id} - Update Category")
    void testUpdateCategory_Success() throws Exception {
        // Given
        Category updatedCategory = Category.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("Updated Programming")
                .description("Updated description")
                .build();

        when(categoryService.updateCategory(eq(1L), any(Category.class)))
                .thenReturn(updatedCategory);

        // When & Then
        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                        .with(csrf())  // Add CSRF token for PUT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Programming")))
                .andExpect(jsonPath("$.description", is("Updated description")));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/category/{id} - Delete Category")
    void testDeleteCategory_Success() throws Exception {
        // Given
        doNothing().when(categoryService).deleteCategory(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/category/{id}", 1L)
                        .with(csrf())  // Add CSRF token for DELETE
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}