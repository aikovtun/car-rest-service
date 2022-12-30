package org.example.repository;

import org.example.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long>, QuerydslPredicateExecutor<Manufacturer> {

    Optional<Manufacturer> findByName(String name);

}
