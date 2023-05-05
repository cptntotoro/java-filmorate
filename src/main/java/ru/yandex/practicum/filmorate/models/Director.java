package ru.yandex.practicum.filmorate.models;

import javax.validation.constraints.NotBlank;
public class Director {

    private int id;

    @NotBlank(message = "Director's name must not be empty.")
    private String name;

    public Director(int id, String name) {
        this(id);
        this.name = name;
    }

    public Director(int id) {
        this.id = id;
    }

    public Director() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
