package org.example.entity;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Set;

@Entity(name = "models")
@AttributeOverride(name = "id", column = @Column(name = "model_id"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Model extends LongEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "year")
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "models_categories",
        joinColumns = @JoinColumn(name = "model_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

}
