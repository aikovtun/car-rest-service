package org.example.sevice.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.entity.Model;
import org.example.repository.ModelRepository;
import org.example.sevice.ModelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;

    @Override
    public long count() {
        return modelRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Model> findById(Long id) {
        return modelRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Model> findByName(String name) {
        return modelRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Model> findAll() {
        return modelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Model> findAll(Predicate predicate, Pageable pageable) {
        return modelRepository.findAll(predicate, pageable);
    }

    @Override
    @Transactional
    public Model save(Model model) {
        return modelRepository.save(model);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        modelRepository.deleteById(id);

    }

}
