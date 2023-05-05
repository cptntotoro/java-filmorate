package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FunctionalityNotImplemetedException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    public int getIdCounter() {
        return ++idCounter;
    }

    @Override
    public Film add(Film film) {
        film.setId(getIdCounter());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        throw new ElementNotFoundException("Film with id = " + film.getId() + " was not found and can not be updated.");
    }

    @Override
    public void remove(Film film) {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new ElementNotFoundException("Film with id = " + id + " was not found.");
    }

    @Override
    public List<Genre> getFilmGenres(Integer filmId) {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public void addLike(Integer userId, Integer filmId) {
        Film film = getById(filmId);
//        User user = getById(userId);
        if (!film.getWhoLiked().contains(userId)) { //&& !user.getFilmsLiked().contains(filmId)) {
            film.addLike(userId);
//            user.addFilmLiked(filmId);
        } else {
            throw new ElementNotFoundException("This user has already liked this film.");
        }
    }

    @Override
    public void removeLike(Integer userId, Integer filmId) {
        Film film = getById(filmId);
//        User user = getById(userId);
        if (film.getWhoLiked().contains(userId)) { // && user.getFilmsLiked().contains(filmId)) {
            film.removeLike(userId);
//            user.removeFilmLiked(filmId);
        } else {
            throw new ElementNotFoundException("This user has not yet liked this film.");
        }
    }

    @Override
    public List<Film> getTopLikedFilms(Integer count) {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public Mpa getMpa(Integer id) {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public List<Mpa> getAllMpa() {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public Genre getGenre(Integer id) {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public List<Genre> getAllGenres() {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
    }

    @Override
    public List<Film> getByDirectorSortedByYear(Integer directorId) {
        return null;
    }

    @Override
    public List<Film> getByDirectorSortedByLikes(Integer directorId) {
        return null;
    }

}
