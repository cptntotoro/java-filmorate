package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    int idCounter = 0;
    Map<Integer, Film> films = new HashMap<>();
    final LocalDate birthdayOfCinema = LocalDate.of(1895, 12, 28);
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    public int getIdCounter() {
        return ++idCounter;
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        if (!isValidFilm(film)) {
            throw new ValidationException("Film data is not valid.");
        }
        film.setId(getIdCounter());
        films.put(film.getId(), film);
        return film;
    }

    private boolean isValidFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Film name is either empty or null.");
            return false;
        }
        if (film.getDescription().length() > 200) {
            log.error("Film description is longer than 200 chars.");
            return false;
        }
        if (film.getDuration() < 0) {
            log.error("Film duration is negative.");
            return false;
        }
        if (film.getReleaseDate().isBefore(birthdayOfCinema)) {
            log.error("Film release date is earlier than the birthday of cinema.");
            return false;
        }
        return true;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!isValidFilm(film)) {
            throw new ValidationException("Film data is not valid.");
        }
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            films.put(film.getId(), film);
            return film;
        }
        log.warn("Film was not found and can not be updated.");
        throw new ValidationException("Film was not found and can not be updated.");
    }

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
