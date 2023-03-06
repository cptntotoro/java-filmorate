package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private int idCounter = 0;
    private Map<Integer, User> users = new HashMap<>();

    public int getIdCounter() {
        return ++idCounter;
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        setNameAsLoginIfMissing(user);
        user.setId(getIdCounter());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        setNameAsLoginIfMissing(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }

        log.warn("User was not found and could not be updated.");
        throw new ValidationException("User with this id was not found and could not be updated.");
    }

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private void setNameAsLoginIfMissing(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
