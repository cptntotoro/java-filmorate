package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.Director;

import java.util.List;

public interface DirectorStorage {

    Director create(Director director);

    Director get(int id);

    Director update(Director director);

    List<Director> getAll();

    void delete(int id);
}
