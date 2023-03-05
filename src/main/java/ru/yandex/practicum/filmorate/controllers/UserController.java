package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    int idCounter = 0;
    Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    public int getIdCounter() {
        return ++idCounter;
    }

    @PostMapping
    public User add(@RequestBody User user) {
        if (!isValidUser(user)) {
            throw new ValidationException("User data is not valid.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Name was not defined and therefore, was set equal to login.");
            user.setName(user.getLogin());
        }
        user.setId(getIdCounter());
        users.put(user.getId(), user);
        return user;
    }

    private boolean isValidUser(User user) {
        if (user.getEmail().isEmpty() || user.getEmail() == "" || !user.getEmail().contains("@")) {
            log.error("Email address is either empty or doesn't contain the '@' symbol.");
            return false;
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Login is either empty or contains backspaces.");
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Birthday is set to a future date.");
            return false;
        }
        return true;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!isValidUser(user)) {
            throw new ValidationException("User data is not valid.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Name was not defined and therefore, was set equal to login.");
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
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
}
