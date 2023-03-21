package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        return filmService.add(film);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable @Positive int id) {
        return filmService.getById(id);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable @Positive int filmId, @PathVariable @Positive int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable @Positive int filmId, @PathVariable @Positive int userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        return filmService.getTopLikedFilms(count);
    }

}
