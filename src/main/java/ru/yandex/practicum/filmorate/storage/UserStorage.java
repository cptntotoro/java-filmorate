package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(User user);

    User getById(Integer id);

    List<User> getAll();
}
