package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Film update(@RequestBody @Valid Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(userId, filmId);
    }

    public Set<Film> getTopLikedFilms(Integer count) {
        return new HashSet<>(filmStorage.getTopLikedFilms(count));
    }

    public Mpa getMpa(Integer mpaId) {
        return filmStorage.getMpa(mpaId);
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Genre getGenre(Integer id) {
        return filmStorage.getGenre(id);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }
}
