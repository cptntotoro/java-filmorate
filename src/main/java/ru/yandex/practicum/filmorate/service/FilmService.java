package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private FilmStorage filmStorage;

    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        if (!film.getWhoLiked().contains(userId) && !user.getFilmsLiked().contains(filmId)) {
            film.addLike(userId);
            user.addFilmLiked(filmId);
        } else {
            throw new ElementNotFoundException("This user has already liked this film.");
        }
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        if (film.getWhoLiked().contains(userId) && user.getFilmsLiked().contains(filmId)) {
            film.removeLike(userId);
            user.removeFilmLiked(filmId);
        } else {
            throw new ElementNotFoundException("This user has not yet liked this film.");
        }
    }

    public Set<Film> getTopLikedFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> o1.getWhoLiked().size() >= o2.getWhoLiked().size() ? -1 : 1)
                .limit(count)
                .collect(Collectors.toSet());
    }
}
