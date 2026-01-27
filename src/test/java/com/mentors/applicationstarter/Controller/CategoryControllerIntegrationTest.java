package com.mentors.applicationstarter.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Category Controller Integration Tests")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        // Clean database
        categoryRepository.deleteAll();

        // Create test data
        category1 = Category.builder()
                .name("Programming")
                .description("Programming courses")
                .build();

        category2 = Category.builder()
                .name("Design")
                .description("Design courses")
                .build();

        category3 = Category.builder()
                .name("Marketing")
                .description("Marketing courses")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Integration: Create Category")
    void testCreateCategory() throws Exception {
        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Programming")))
                .andExpect(jsonPath("$.description", is("Programming courses")));

        Assertions.assertEquals(1, categoryRepository.count());
    }

    @Test
    @Order(2)
    @DisplayName("Integration: Get All Categories")
    void testGetAllCategories() throws Exception {
        // Given - save categories to database
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // When & Then
        mockMvc.perform(get("/api/v1/category/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("Programming", "Design", "Marketing")));
    }

    @Test
    @Order(3)
    @DisplayName("Integration: Get Paged Categories")
    void testGetPagedCategories() throws Exception {
        // Given
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // When & Then - Get first page with 2 items
        mockMvc.perform(get("/api/v1/category")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.content[0].name", is("Design")))
                .andExpect(jsonPath("$.content[1].name", is("Marketing")));
    }

    @Test
    @Order(4)
    @DisplayName("Integration: Get Category By Id")
    void testGetCategoryById() throws Exception {
        // Given
        Category saved = categoryRepository.save(category1);

        // When & Then
        mockMvc.perform(get("/api/v1/category/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Programming")));
    }

    @Test
    @Order(5)
    @DisplayName("Integration: Update Category")
    void testUpdateCategory() throws Exception {
        // Given
        Category saved = categoryRepository.save(category1);

        Category updateData = Category.builder()
                .name("Advanced Programming")
                .description("Advanced programming courses")
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/category/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Advanced Programming")))
                .andExpect(jsonPath("$.description", is("Advanced programming courses")));

        // Verify in database
        Category updated = categoryRepository.findById(saved.getId()).orElseThrow();
        Assertions.assertEquals("Advanced Programming", updated.getName());
    }

    @Test
    @Order(6)
    @DisplayName("Integration: Delete Category")
    void testDeleteCategory() throws Exception {
        // Given
        Category saved = categoryRepository.save(category1);
        Long categoryId = saved.getId();

        // When & Then
        mockMvc.perform(delete("/api/v1/category/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify deletion
        Assertions.assertFalse(categoryRepository.existsById(categoryId));
    }

    @Test
    @Order(7)
    @DisplayName("Integration: Search Categories by Name")
    void testSearchCategoriesByName() throws Exception {
        // Given
        categoryRepository.save(category1); // Programming
        categoryRepository.save(category2); // Design
        categoryRepository.save(category3); // Marketing

        // When & Then - Search for "Design"
        mockMvc.perform(get("/api/v1/category")
                        .param("name", "Design")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Design")));
    }

    @Test
    @Order(8)
    @DisplayName("Integration: Pagination - Second Page")
    void testPagination_SecondPage() throws Exception {
        // Given
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // When & Then - Get second page
        mockMvc.perform(get("/api/v1/category")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Programming")))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @Order(9)
    @DisplayName("Integration: Sort Categories Descending")
    void testSortDescending() throws Exception {
        // Given
        categoryRepository.save(category1); // Programming
        categoryRepository.save(category2); // Design
        categoryRepository.save(category3); // Marketing

        // When & Then
        mockMvc.perform(get("/api/v1/category")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name", is("Programming")))
                .andExpect(jsonPath("$.content[1].name", is("Marketing")))
                .andExpect(jsonPath("$.content[2].name", is("Design")));
    }
}