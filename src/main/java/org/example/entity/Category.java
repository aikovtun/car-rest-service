package org.example.entity;

import lombok.*;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity(name = "categories")
@AttributeOverride(name = "id", column = @Column(name = "category_id"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Category extends LongEntity {

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Model> models;

}
