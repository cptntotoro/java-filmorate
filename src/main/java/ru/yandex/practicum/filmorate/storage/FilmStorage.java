package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    void remove(Film film);

    List<Film> getAll();

    Film getById(int id);

    List<Genre> getFilmGenres(Integer filmId);

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer userId, Integer filmId);

    List<Film> getTopLikedFilms(Integer count);

    Mpa getMpa(Integer id);

    List<Mpa> getAllMpa();

    Genre getGenre(Integer id);

    List<Genre> getAllGenres();

    List<Film> getByDirectorSortedByYear(Integer directorId);

    List<Film> getByDirectorSortedByLikes(Integer directorId);
}
