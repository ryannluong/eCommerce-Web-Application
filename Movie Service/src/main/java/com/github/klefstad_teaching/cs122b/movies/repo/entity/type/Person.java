package com.github.klefstad_teaching.cs122b.movies.repo.entity.type;

public class Person {
    private String name;
    Long id;

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Person setId(Long id) {
        this.id = id;
        return this;
    }
}
