package org.example.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ModelDto {

    Long id;
    String name;
    String uuid;
    Integer year;
    ManufacturerDto manufacturer;
    Set<CategoryDto> categories;

}
