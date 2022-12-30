package org.example.entity;

import lombok.*;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity(name = "manufacturers")
@AttributeOverride(name = "id", column = @Column(name = "manufacturer_id"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Manufacturer extends LongEntity {

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "manufacturer")
    private Set<Model> models;

}
