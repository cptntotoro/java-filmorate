package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@RequestMapping
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film add(@RequestBody @Valid Film film) {
        return filmService.add(film);
    }

    @GetMapping("/films/{id}")
    public Film getById(@PathVariable @Positive int id) {
        return filmService.getById(id);
    }

    @PutMapping("/films")
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @GetMapping("/films")
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void addLike(@PathVariable @Positive int filmId, @PathVariable @Positive int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void removeLike(@PathVariable @Positive int filmId, @PathVariable @Positive int userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Set<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        return filmService.getTopLikedFilms(count);
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa getMpa(@PathVariable @Positive int mpaId) {
        return filmService.getMpa(mpaId);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenre(@PathVariable @Positive int genreId) {
        return filmService.getGenre(genreId);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getByYearAndLikes(@PathVariable int directorId, @RequestParam("sortBy") String value) {
        return filmService.getByYearAndLikes(directorId, value);
    }
}
