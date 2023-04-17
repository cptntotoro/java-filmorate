package ru.yandex.practicum.filmorate.models;

import javax.validation.constraints.Positive;

public class Mpa {

    private String name;

    @Positive
    private Integer id;

    public Mpa() {
    }

    public Mpa(Integer id) {
        this.id = id;
    }

    public Mpa(Integer id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
