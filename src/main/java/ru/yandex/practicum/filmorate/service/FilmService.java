package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class FilmService {

    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Autowired
    @Qualifier("directorDbStorage")
    private DirectorStorage directorStorage;

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

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

    public List<Film> getByYearAndLikes(int directorId, String value) {
        Director director = directorStorage.get(directorId);
        if (director == null) {
            log.error("Director with id = " + directorId + " doesn't exist.");
            throw new NullPointerException("Director with id = " + directorId + " doesn't exist.");
        }

        if (Objects.equals(value, "year")) {
            return filmStorage.getByDirectorSortedByYear(directorId);
        } else if (Objects.equals(value, "likes")) {
            return filmStorage.getByDirectorSortedByLikes(directorId);
        } else {
            log.error("Parameter sortBy = " + value + " doesn't exist.");
            throw new NullPointerException();
        }
    }
}
