package org.example.config;

import org.example.dto.CategoryDto;
import org.example.dto.ManufacturerDto;
import org.example.dto.ModelDto;
import org.example.entity.Category;
import org.example.entity.Manufacturer;
import org.example.entity.Model;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(CategoryDto.class, Category.class)
            .addMappings(mapper -> mapper.skip(Category::setId));
        modelMapper.typeMap(ManufacturerDto.class, Manufacturer.class)
            .addMappings(mapper -> mapper.skip(Manufacturer::setId));
        modelMapper.typeMap(ModelDto.class, Model.class)
            .addMappings(mapper -> mapper.skip(Model::setId));
        return new ModelMapper();
    }

}
