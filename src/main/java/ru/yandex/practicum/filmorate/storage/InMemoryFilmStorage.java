package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    public final static LocalDate BIRTHDAY_OF_CINEMA = LocalDate.of(1895, 12, 28);
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
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new ElementNotFoundException("Film with id = " + id + " was not found.");
    }
}
