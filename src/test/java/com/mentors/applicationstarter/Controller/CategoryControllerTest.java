package com.mentors.applicationstarter.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentors.applicationstarter.Configuration.JwtAuthenticationFilter;
import com.mentors.applicationstarter.DTO.CategoryDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Service.CategoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryDTO categoryDTO1;
    private CategoryDTO categoryDTO2;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("POST /api/v1/category - Create Category")
    void testCreateCategory_Success() throws Exception {
        // Given
        Category categoryRequest = new Category();
        categoryRequest.setName("Programming");
        categoryRequest.setDescription("Programming courses");

        CategoryDTO expectedResponse = CategoryDTO.builder()
                .id(1L)
                .name("Programming")
                .description("Programming courses")
                .build();

        // Mock the service call
        when(categoryService.createCategory(any(Category.class)))
                .thenReturn(expectedResponse);

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/category")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Programming")))
                .andExpect(jsonPath("$.description", is("Programming courses")))
                .andReturn();

        // Additional debug
        System.out.println("=== TEST DEBUG ===");
        System.out.println("Response Status: " + result.getResponse().getStatus());
        System.out.println("Response Body: " + result.getResponse().getContentAsString());

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    @DisplayName("POST /api/v1/category - Create Category with Invalid Data")
    void testCreateCategory_InvalidData() throws Exception {
        // Given - empty name should fail validation
        Category categoryRequest = new Category();
        categoryRequest.setName(""); // Invalid
        categoryRequest.setDescription("Some description");

        // When & Then
        mockMvc.perform(post("/api/v1/category")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(Category.class));
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
                .andExpect(jsonPath("$[0].name", is("Programming")))
                .andExpect(jsonPath("$[1].name", is("Design")));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/v1/category/{id} - Category Not Found")
    void testGetCategoryById_NotFound() throws Exception {
        // Given
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException(ErrorCodes.CATEGORY_DOES_NOT_EXIST));

        // When & Then
        mockMvc.perform(get("/api/v1/category/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(999L);
    }
}