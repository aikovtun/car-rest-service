package org.example.sevice.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.entity.Manufacturer;
import org.example.repository.ManufacturerRepository;
import org.example.sevice.ManufacturerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Manufacturer> findById(Long id) {
        return manufacturerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Manufacturer> findByName(String name) {
        return manufacturerRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Manufacturer> findAll() {
        return manufacturerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Manufacturer> findAll(Predicate predicate, Pageable pageable) {
        return manufacturerRepository.findAll(predicate, pageable);
    }

    @Override
    @Transactional
    public Manufacturer save(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        manufacturerRepository.deleteById(id);
    }

}
