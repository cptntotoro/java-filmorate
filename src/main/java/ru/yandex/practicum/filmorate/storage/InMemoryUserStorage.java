package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int idCounter = 0;

    private Map<Integer, User> users = new HashMap<>();

    public int getIdCounter() {
        return ++idCounter;
    }

    @Override
    public User add(User user) {
        user.setId(getIdCounter());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new ElementNotFoundException("User with id = " + user.getId() + " was not found and could not be updated.");
    }

    @Override
    public User getById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new ElementNotFoundException("User with id = " + id + " was not found.");
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
