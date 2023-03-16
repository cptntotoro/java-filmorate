package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private int idCounter = 0;
    private Map<Integer, Film> films = new HashMap<>();
    public final static LocalDate BIRTHDAY_OF_CINEMA = LocalDate.of(1895, 12, 28);

    public int getIdCounter() {
        return ++idCounter;
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        film.setId(getIdCounter());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        if (films.containsKey(film.getId())) {
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
