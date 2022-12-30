package org.example.controller.v1.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import org.example.config.ApplicationConfig;
import org.example.config.WebSecurityConfig;
import org.example.entity.Manufacturer;
import org.example.sevice.ManufacturerService;
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

@WebMvcTest(controllers = ManufacturerControllerImpl.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Import({ApplicationConfig.class, WebSecurityConfig.class})
public class ManufacturerControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @MockBean
    private ManufacturerService manufacturerService;

    private static final List<Manufacturer> manufacturers = Arrays.asList(
        new Manufacturer("Toyota", null),
        new Manufacturer("Porsche", null),
        new Manufacturer("Nissan", null),
        new Manufacturer("Mercedes-Benz", null),
        new Manufacturer("MAZDA", null)
    );

    @Test
    @DisplayName("get:api/v1/manufacturers - should return correct data")
    public void findAllShouldReturnCorrectData() throws Exception {
        when(manufacturerService.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(new PageImpl<>(manufacturers));
        mockMvc.perform(get("/api/v1/manufacturers")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content.size()", Matchers.is(5)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @DisplayName("get:api/v1/manufacturers/{id} - should return correct data")
    public void findByIdShouldReturnCorrectData(long id) throws Exception {
        Manufacturer manufacturer = manufacturers.get((int) (id - 1));
        when(manufacturerService.findById(manufacturer.getId())).thenReturn(Optional.of(manufacturer));
        mockMvc.perform(get("/api/v1/manufacturers/{id}", manufacturer.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.name", Matchers.is(manufacturer.getName())));
    }

    @Test
    @DisplayName("get:api/v1/manufacturers/{id} - should throw 404")
    public void findByIdShouldThrow404() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        when(manufacturerService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/manufacturers/{id}", manufacturer.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("post:api/v1/manufacturers - should insert correct data to database")
    public void createShouldInsertCorrectDataToDatabase(long id) throws Exception {
        Manufacturer manufacturer = manufacturers.get((int) (id - 1));
        when(manufacturerService.save(any())).thenReturn(manufacturer);
        mockMvc.perform(post("/api/v1/manufacturers/")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manufacturer))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", Matchers.equalTo(manufacturer.getName())));
        verify(manufacturerService, times(1)).save(any());
    }

    @Test
    @DisplayName("post:api/v1/manufacturers - should throw 403 for unauthorized")
    public void createShouldThrow401() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        mockMvc.perform(post("/api/v1/manufacturers/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manufacturer))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(manufacturerService, times(0)).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("put:api/v1/manufacturers/{id} - should update data id database")
    public void updateShouldUpdateDataInDatabase(long id) throws Exception {
        Manufacturer manufacturer = manufacturers.get((int) (id - 1));
        when(manufacturerService.findById(manufacturer.getId())).thenReturn(Optional.of(manufacturer));
        when(manufacturerService.save(any())).thenReturn(manufacturer);
        mockMvc.perform(put("/api/v1/manufacturers/{id}", manufacturer.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manufacturer))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", Matchers.equalTo(manufacturer.getName())));
        verify(manufacturerService, times(1)).save(any());
    }

    @Test
    @DisplayName("put:api/v1/manufacturers/{id} - should throw 403 for unauthorized")
    public void updateShouldThrow401() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        mockMvc.perform(put("/api/v1/manufacturers/{id}", manufacturer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manufacturer))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(manufacturerService, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("put:api/v1/manufacturers/{id} - should throw 404")
    public void updateShouldThrow404() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        when(manufacturerService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/v1/manufacturers/{id}", manufacturer.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manufacturer))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(manufacturerService, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("delete:api/v1/manufacturers/{id} - should delete data from database")
    public void deleteShouldDeleteDataFromDatabase() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        mockMvc.perform(delete("/api/v1/manufacturers/{id}", manufacturer.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andDo(print())
            .andExpect(status().isNoContent());
        verify(manufacturerService, times(1)).deleteById(manufacturer.getId());
    }

    @Test
    @DisplayName("delete:api/v1/manufacturers/{id} - should throw 403 for unauthorized")
    public void deleteShouldThrow401() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        mockMvc.perform(delete("/api/v1/manufacturers/", manufacturer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manufacturer))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(manufacturerService, times(0)).deleteById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("delete:api/v1/manufacturers/{id} - should throw 404")
    public void deleteShouldThrow404() throws Exception {
        Manufacturer manufacturer = manufacturers.get(0);
        doThrow(new EmptyResultDataAccessException(0)).when(manufacturerService).deleteById(manufacturer.getId());
        mockMvc.perform(delete("/api/v1/manufacturers/{id}", manufacturer.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(manufacturerService, times(1)).deleteById(manufacturer.getId());
    }

}
