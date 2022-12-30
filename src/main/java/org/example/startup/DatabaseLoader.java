package org.example.startup;

import lombok.RequiredArgsConstructor;
import org.example.entity.Category;
import org.example.entity.Manufacturer;
import org.example.entity.Model;
import org.example.sevice.CategoryService;
import org.example.sevice.ManufacturerService;
import org.example.sevice.ModelService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class DatabaseLoader implements ApplicationRunner {

    private final ManufacturerService manufacturerService;
    private final CategoryService categoryService;
    private final ModelService modelService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        if (modelService.count() != 0) {
            return;
        }

        List<String[]> lines = new ArrayList<>();
        Resource resource = new ClassPathResource("database.csv");
        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(resource.getInputStream()))) {
            bufferedReader.lines().forEach(line -> lines.add(line.split(",")));
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i);
            Model model = new Model();
            model.setUuid(values[0]);
            model.setManufacturer(getManufacturer(values[1]));
            model.setYear(Integer.valueOf(values[2]));
            model.setName(values[3]);
            model.setCategories(getCategories(values));
            modelService.save(model);
        }
    }

    private Manufacturer getManufacturer(String name) {
        Optional<Manufacturer> optionalManufacturer = manufacturerService.findByName(name);
        return optionalManufacturer.orElseGet(
            () -> manufacturerService.save(new Manufacturer(name, null)));
    }

    private Set<Category> getCategories(String[] values) {
        Set<Category> categories = new HashSet<>();
        for (int i = 4; i < values.length; i++) {
            String name = values[i].replace("\"", "").trim();
            Optional<Category> optionalCategory = categoryService.findByName(name);
            if (optionalCategory.isPresent()) {
                categories.add(optionalCategory.get());
            } else {
                categories.add(categoryService.save(new Category(name, null)));
            }
        }
        return categories;
    }

}
