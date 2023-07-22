package com.geopokrovskiy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String filePath;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "file")
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    @JsonIgnore
    @ToString.Exclude
    private List<Event> events;
}
