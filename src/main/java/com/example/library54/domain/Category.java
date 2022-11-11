package com.example.library54.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "tbl_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 80, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean builtIn=false;

    @Column(nullable = false)
    private int sequence;

    @OneToMany(mappedBy="categoryId")
    @JsonIgnoreProperties("categoryId")
    private List<Book> books;
}
