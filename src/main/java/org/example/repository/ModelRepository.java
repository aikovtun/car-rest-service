package org.example.repository;

import org.example.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long>, QuerydslPredicateExecutor<Model> {

    Optional<Model> findByName(String name);

}
