package org.example.sevice;

import com.querydsl.core.types.Predicate;
import org.example.entity.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ManufacturerService {

    Optional<Manufacturer> findById(Long id);

    Optional<Manufacturer> findByName(String name);

    List<Manufacturer> findAll();

    Page<Manufacturer> findAll(Predicate predicate, Pageable pageable);

    Manufacturer save(Manufacturer manufacturer);

    void deleteById(Long id);

}
