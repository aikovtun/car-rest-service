package org.example.controller.v1.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.controller.v1.CategoryController;
import org.example.dto.CategoryDto;
import org.example.entity.Category;
import org.example.sevice.CategoryService;
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
public class CategoryControllerImpl implements CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper mapper;

    @Override
    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryDto>> findAll(
        @QuerydslPredicate(root = Category.class) Predicate predicate, Pageable pageable) {
        Page<CategoryDto> pageCategories = categoryService.findAll(predicate, pageable)
            .map(c -> mapper.map(c, CategoryDto.class));
        if (pageCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageCategories);
    }

    @Override
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable(name = "id") Long id) {
        Optional<CategoryDto> optionalCategory = categoryService.findById(id)
            .map(c -> mapper.map(c, CategoryDto.class));
        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(optionalCategory.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Category with id=%s not found", id));
        }
    }

    @Override
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto categoryDto) {
        Category category = categoryService.save(mapper.map(categoryDto, Category.class));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.map(category, CategoryDto.class));
    }

    @Override
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> update(
        @PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        Optional<Category> optionalCategory = categoryService.findById(id);
        if (optionalCategory.isEmpty()) {
            String message = String.format("Category with id=%s not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }

        Category category = optionalCategory.get();
        mapper.map(categoryDto, category);
        return ResponseEntity.status(HttpStatus.OK)
            .body(mapper.map(categoryService.save(category), CategoryDto.class));
    }

    @Override
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        try {
            categoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            String massage = String.format("Category with id=%s not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, massage);
        }
    }

}
