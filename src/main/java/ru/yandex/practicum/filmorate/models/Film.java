package ru.yandex.practicum.filmorate.models;

import ru.yandex.practicum.filmorate.annotations.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Film implements Serializable {

    private int id;

    @NotBlank(message = "Film name is either empty or null.")
    private String name;

    @NotNull
    @Size(max = 200, message = "Film description is longer than 200 chars.")
    private String description;

    @ReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Film duration is negative.")
    private int duration;

    private Set<Integer> whoLiked = new HashSet<>();

    public Film() {
    }

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this(name, description, releaseDate, duration);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && duration == film.duration && name.equals(film.name) &&
                description.equals(film.description) && releaseDate.equals(film.releaseDate) &&
                Objects.equals(whoLiked, film.whoLiked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration, whoLiked);
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Set<Integer> getWhoLiked() {
        return whoLiked;
    }

    public void setWhoLiked(Set<Integer> whoLiked) {
        this.whoLiked = whoLiked;
    }

    public void addLike(Integer userId) {
        whoLiked.add(userId);
    }

    public void removeLike(Integer userId) {
        whoLiked.remove(userId);
    }
}
