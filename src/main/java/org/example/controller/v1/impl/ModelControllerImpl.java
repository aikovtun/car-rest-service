package org.example.controller.v1.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.controller.v1.ModelController;
import org.example.dto.ModelDto;
import org.example.entity.Manufacturer;
import org.example.entity.Model;
import org.example.entity.QModel;
import org.example.sevice.ManufacturerService;
import org.example.sevice.ModelService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1", produces = "application/json")
@RequiredArgsConstructor
public class ModelControllerImpl implements ModelController {

    private final ModelService modelService;
    private final ManufacturerService manufacturerService;
    private final ModelMapper mapper;

    @Override
    @GetMapping("/models")
    public ResponseEntity<Page<ModelDto>> findAll(
        @QuerydslPredicate(root = Model.class) Predicate predicate, Pageable pageable) {
        Page<ModelDto> pageModel = modelService.findAll(predicate, pageable)
            .map(m -> mapper.map(m, ModelDto.class));
        if (pageModel.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageModel);
    }

    @Override
    @GetMapping("/manufacturers/{manufacturerName}/models")
    public ResponseEntity<Page<ModelDto>> findAllByManufacturer(@PathVariable String manufacturerName,
        @QuerydslPredicate(root = Model.class) Predicate predicate, Pageable pageable) {
        Predicate modifiedPredicate = QModel.model.manufacturer.name.eq(manufacturerName).and(predicate);
        Page<ModelDto> pageModel = modelService.findAll(modifiedPredicate, pageable)
            .map(m -> mapper.map(m, ModelDto.class));
        if (pageModel.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageModel);
    }

    @Override
    @GetMapping("/models/{id}")
    public ResponseEntity<ModelDto> findById(@PathVariable(name = "id") Long id) {
        Optional<ModelDto> optionalModel = modelService.findById(id)
            .map(m -> mapper.map(m, ModelDto.class));
        if (optionalModel.isPresent()) {
            return ResponseEntity.ok(optionalModel.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Model with id=%s not found", id));
        }
    }

    @Override
    @PostMapping("/models")
    public ResponseEntity<ModelDto> create(@RequestBody ModelDto modelDto) {
        Model model = modelService.save(mapper.map(modelDto, Model.class));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.map(model, ModelDto.class));
    }

    @Override
    @PostMapping("/manufacturers/{manufacturerName}/models")
    public ResponseEntity<ModelDto> createByManufacturer(
        @PathVariable String manufacturerName, @RequestBody ModelDto modelDto) {
        Optional<Manufacturer> manufacturer = manufacturerService.findByName(manufacturerName);
        if (manufacturer.isEmpty()) {
            String message = String.format("Manufacturer with name=%s not found", manufacturerName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        Model model = mapper.map(modelDto, Model.class);
        model.setManufacturer(manufacturer.get());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.map(modelService.save(model), ModelDto.class));
    }

    @Override
    @PutMapping("/models/{id}")
    public ResponseEntity<ModelDto> update(@PathVariable Long id, @RequestBody ModelDto modelDto) {
        Optional<Model> optionalModel = modelService.findById(id);
        if (optionalModel.isEmpty()) {
            String message = String.format("Model with id=%s not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }

        Model model = optionalModel.get();
        mapper.map(modelDto, model);
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(modelService.save(model), ModelDto.class));
    }

    @Override
    @DeleteMapping("/models/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        try {
            modelService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            String massage = String.format("Model with id=%s not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, massage);
        }
    }

}
