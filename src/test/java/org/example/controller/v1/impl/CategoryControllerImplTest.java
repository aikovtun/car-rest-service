package org.example.controller.v1.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import org.example.config.ApplicationConfig;
import org.example.config.WebSecurityConfig;
import org.example.entity.Category;
import org.example.sevice.CategoryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryControllerImpl.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Import({ApplicationConfig.class, WebSecurityConfig.class})
public class CategoryControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @MockBean
    private CategoryService categoryService;

    private static final List<Category> categories = Arrays.asList(
        new Category("Sedan", null),
        new Category("SUV", null),
        new Category("Pickup", null),
        new Category("Coupe", null),
        new Category("Hatchback", null)
    );

    @Test
    @DisplayName("get:api/v1/categories - should return correct data")
    public void findAllShouldReturnCorrectData() throws Exception {
        when(categoryService.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(new PageImpl<>(categories));
        mockMvc.perform(get("/api/v1/categories")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content.size()", Matchers.is(5)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @DisplayName("get:api/v1/categories/{id} - should return correct data")
    public void findByIdShouldReturnCorrectData(long id) throws Exception {
        Category category = categories.get((int) (id - 1));
        when(categoryService.findById(category.getId())).thenReturn(Optional.of(category));
        mockMvc.perform(get("/api/v1/categories/{id}", category.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.name", Matchers.is(category.getName())));
    }

    @Test
    @DisplayName("get:api/v1/categories/{id} - should throw 404")
    public void findByIdShouldThrow404() throws Exception {
        Category category = categories.get(0);
        when(categoryService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/categories/{id}", category.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("post:api/v1/categories - should insert correct data to database")
    public void createShouldInsertCorrectDataToDatabase(long id) throws Exception {
        Category category = categories.get((int) (id - 1));
        when(categoryService.save(any())).thenReturn(category);
        mockMvc.perform(post("/api/v1/categories/")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", Matchers.equalTo(category.getName())));
        verify(categoryService, times(1)).save(any());
    }

    @Test
    @DisplayName("post:api/v1/categories - should throw 403 for unauthorized")
    public void createShouldThrow401() throws Exception {
        Category category = categories.get(0);
        mockMvc.perform(post("/api/v1/categories/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(categoryService, times(0)).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("put:api/v1/categories/{id} - should update data id database")
    public void updateShouldUpdateDataInDatabase(long id) throws Exception {
        Category category = categories.get((int) (id - 1));
        when(categoryService.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryService.save(any())).thenReturn(category);
        mockMvc.perform(put("/api/v1/categories/{id}", category.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", Matchers.equalTo(category.getName())));
        verify(categoryService, times(1)).save(any());
    }

    @Test
    @DisplayName("put:api/v1/categories - should throw 403 for unauthorized")
    public void updateShouldThrow401() throws Exception {
        Category category = categories.get(0);
        mockMvc.perform(put("/api/v1/categories/{id}", category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(categoryService, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("put:api/v1/categories/{id} - should throw 404")
    public void updateShouldThrow404() throws Exception {
        Category category = categories.get(0);
        when(categoryService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/v1/categories/{id}", category.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(categoryService, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("delete:api/v1/categories/{id} - should delete data from database")
    public void deleteShouldDeleteDataFromDatabase() throws Exception {
        Category category = categories.get(0);
        mockMvc.perform(delete("/api/v1/categories/{id}", category.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andDo(print())
            .andExpect(status().isNoContent());
        verify(categoryService, times(1)).deleteById(category.getId());
    }

    @Test
    @DisplayName("delete:api/v1/categories/{id} - should throw 403 for unauthorized")
    public void deleteShouldThrow401() throws Exception {
        Category category = categories.get(0);
        mockMvc.perform(delete("/api/v1/categories/{id}", category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(categoryService, times(0)).deleteById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("delete:api/v1/categories{id} - should throw 404")
    public void deleteShouldThrow404() throws Exception {
        Category category = categories.get(0);
        doThrow(new EmptyResultDataAccessException(0)).when(categoryService).deleteById(category.getId());
        mockMvc.perform(delete("/api/v1/categories/{id}", category.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(categoryService, times(1)).deleteById(category.getId());
    }

}
