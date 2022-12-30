package org.example.sevice;

import com.querydsl.core.types.Predicate;
import org.example.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

    List<Category> findAll();

    Page<Category> findAll(Predicate predicate, Pageable pageable);

    Category save(Category category);

    void deleteById(Long id);

}
