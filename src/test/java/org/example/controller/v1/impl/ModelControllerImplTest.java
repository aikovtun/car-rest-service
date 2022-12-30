package org.example.controller.v1.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import org.example.config.ApplicationConfig;
import org.example.config.WebSecurityConfig;
import org.example.entity.Manufacturer;
import org.example.entity.Model;
import org.example.sevice.ManufacturerService;
import org.example.sevice.ModelService;
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

@WebMvcTest(controllers = ModelControllerImpl.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Import({ApplicationConfig.class, WebSecurityConfig.class})
public class ModelControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @MockBean
    private ModelService modelService;

    @MockBean
    private ManufacturerService manufacturerService;

    private static final List<Model> models = Arrays.asList(
        new Model("Q3", "ZRgPP9dBMm", 2020, new Manufacturer("Audi", null), null),
        new Model("Malibu", "cptB1C1NSL", 2020, new Manufacturer("Chevrolet", null), null),
        new Model("Escalade ESV", "ElhqsRZDnP", 2020, new Manufacturer("Cadillac", null), null),
        new Model("3 Series", "7G1VT2pSNO", 2020, new Manufacturer("BMW", null), null),
        new Model("Pacifica", "4q7L9FAU2S", 2020, new Manufacturer("Chrysler", null), null));

    @Test
    @DisplayName("get:api/v1/models - should return correct data")
    public void findAllShouldReturnCorrectData() throws Exception {
        when(modelService.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(new PageImpl<>(models));
        mockMvc.perform(get("/api/v1/models")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content.size()", Matchers.is(5)));
    }

    @Test
    @DisplayName("get:api/v1//manufacturers/{name}/models/ - should return correct data")
    public void findAllByManufacturerShouldReturnCorrectData() throws Exception {
        when(modelService.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(new PageImpl<>(models));
        mockMvc.perform(get("/api/v1/manufacturers/name/models/")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content.size()", Matchers.is(5)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @DisplayName("get:api/v1/models{id} - should return correct data")
    public void findByIdShouldReturnCorrectData(long id) throws Exception {
        Model model = models.get((int) (id - 1));
        when(modelService.findById(model.getId())).thenReturn(Optional.of(model));
        mockMvc.perform(get("/api/v1/models/{id}", model.getId())
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.name", Matchers.is(model.getName())));
    }

    @Test
    @DisplayName("get:api/v1/models/{id} - should throw 404")
    public void findByIdShouldThrow404() throws Exception {
        Model model = models.get(0);
        when(modelService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/models/{id}", model.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("post:api/v1/models - should insert correct data to database")
    public void createShouldInsertCorrectDataToDatabase(long id) throws Exception {
        Model model = models.get((int) (id - 1));
        when(modelService.save(any())).thenReturn(model);
        mockMvc.perform(post("/api/v1/models/")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", Matchers.equalTo(model.getName())));
        verify(modelService, times(1)).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("post:api/v1/manufacturers/{name}/models - should insert correct data to database")
    public void createByManufacturerShouldInsertCorrectDataToDatabase(long id) throws Exception {
        Model model = models.get((int) (id - 1));
        Manufacturer manufacturer = new Manufacturer("test", null);
        when(modelService.save(any())).thenReturn(model);
        when(manufacturerService.findByName(any())).thenReturn(Optional.of(manufacturer));
        mockMvc.perform(post("/api/v1//manufacturers/{name}/models/", manufacturer.getName())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", Matchers.equalTo(model.getName())));
        verify(modelService, times(1)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("post:api/v1/manufacturers/{name}/models - should throw 404")
    public void createByManufacturerShouldThrow404() throws Exception {
        Model model = models.get(0);
        Manufacturer manufacturer = new Manufacturer("test", null);
        when(modelService.save(any())).thenReturn(model);
        when(manufacturerService.findByName(any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/v1//manufacturers/{name}/models/", manufacturer.getName())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(modelService, times(0)).save(any());
    }

    @Test
    @DisplayName("post:api/v1/models - should throw 403 for unauthorized")
    public void createShouldThrow401() throws Exception {
        Model model = models.get(0);
        mockMvc.perform(post("/api/v1/models/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(modelService, times(0)).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    @WithMockUser
    @DisplayName("put:api/v1/models/{id} - should update data id database")
    public void updateShouldUpdateDataInDatabase(long id) throws Exception {
        Model model = models.get((int) (id - 1));
        when(modelService.findById(model.getId())).thenReturn(Optional.of(model));
        when(modelService.save(any())).thenReturn(model);
        mockMvc.perform(put("/api/v1/models/{id}", model.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", Matchers.equalTo(model.getName())));
        verify(modelService, times(1)).save(any());
    }

    @Test
    @DisplayName("put:api/v1/models/{id} - should throw 403 for unauthorized")
    public void updateShouldThrow401() throws Exception {
        Model model = models.get(0);
        mockMvc.perform(put("/api/v1/models/", model.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(modelService, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("put:api/v1/models/{id} - should throw 404")
    public void updateShouldThrow404() throws Exception {
        Model model = models.get(0);
        when(modelService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/v1/models/{id}", model.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(modelService, times(0)).save(any());
    }

    @Test
    @WithMockUser
    @DisplayName("delete:api/v1/models/{id} - should delete data from database")
    public void deleteShouldDeleteDataFromDatabase() throws Exception {
        Model model = models.get(0);
        mockMvc.perform(delete("/api/v1/models/{id}", model.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andDo(print())
            .andExpect(status().isNoContent());
        verify(modelService, times(1)).deleteById(model.getId());
    }

    @Test
    @DisplayName("delete:api/v1/models/{id} - should throw 403 for unauthorized")
    public void deleteShouldThrow401() throws Exception {
        Model model = models.get(0);
        mockMvc.perform(delete("/api/v1/models/{id}", model.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
        verify(modelService, times(0)).deleteById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("delete:api/v1/models/{id} - should throw 404")
    public void deleteShouldThrow404() throws Exception {
        Model model = models.get(0);
        doThrow(new EmptyResultDataAccessException(0)).when(modelService).deleteById(model.getId());
        mockMvc.perform(delete("/api/v1/models/{id}", model.getId())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andDo(print())
            .andExpect(status().isNotFound());
        verify(modelService, times(1)).deleteById(model.getId());
    }

}
