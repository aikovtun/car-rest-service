package org.example.sevice;

import com.querydsl.core.types.Predicate;
import org.example.entity.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ModelService {

    long count();

    Optional<Model> findById(Long id);

    Optional<Model> findByName(String name);

    List<Model> findAll();

    Page<Model> findAll(Predicate predicate, Pageable pageable);

    Model save(Model model);

    void deleteById(Long id);

}
