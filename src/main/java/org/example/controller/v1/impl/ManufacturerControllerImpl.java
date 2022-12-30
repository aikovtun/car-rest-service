package org.example.controller.v1.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.controller.v1.ManufacturerController;
import org.example.dto.ManufacturerDto;
import org.example.entity.Manufacturer;
import org.example.sevice.ManufacturerService;
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
public class ManufacturerControllerImpl implements ManufacturerController {

    private final ManufacturerService manufacturerService;
    private final ModelMapper mapper;

    @Override
    @GetMapping("/manufacturers")
    public ResponseEntity<Page<ManufacturerDto>> findAll(
        @QuerydslPredicate(root = Manufacturer.class) Predicate predicate, Pageable pageable) {
        Page<ManufacturerDto> pageManufacturer = manufacturerService.findAll(predicate, pageable)
            .map(m -> mapper.map(m, ManufacturerDto.class));
        if (pageManufacturer.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageManufacturer);
    }

    @Override
    @GetMapping("/manufacturers/{id}")
    public ResponseEntity<ManufacturerDto> findById(@PathVariable(name = "id") Long id) {
        Optional<ManufacturerDto> optionalManufacturer = manufacturerService.findById(id)
            .map(m -> mapper.map(m, ManufacturerDto.class));
        if (optionalManufacturer.isPresent()) {
            return ResponseEntity.ok(optionalManufacturer.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Manufacturer with id=%s not found", id));
        }
    }

    @Override
    @PostMapping("/manufacturers")
    public ResponseEntity<ManufacturerDto> create(@RequestBody ManufacturerDto manufacturerDto) {
        Manufacturer manufacturer = manufacturerService.save(mapper.map(manufacturerDto, Manufacturer.class));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.map(manufacturer, ManufacturerDto.class));
    }

    @Override
    @PutMapping("/manufacturers/{id}")
    public ResponseEntity<ManufacturerDto> update(
        @PathVariable Long id, @RequestBody ManufacturerDto manufacturerDto) {
        Optional<Manufacturer> optionalManufacturer = manufacturerService.findById(id);
        if (optionalManufacturer.isEmpty()) {
            String message = String.format("Manufacturer with id=%s not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }

        Manufacturer manufacturer = optionalManufacturer.get();
        mapper.map(manufacturerDto, manufacturer);
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(manufacturerService.save(manufacturer), ManufacturerDto.class));
    }

    @Override
    @DeleteMapping("/manufacturers/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        try {
            manufacturerService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            String massage = String.format("Manufacturer with id=%s not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, massage);
        }
    }

}
