package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        return userService.add(user);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable @Positive int id) {
        return userService.getById(id);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable @Positive int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public Set<User> getCommonFriends(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        return userService.getCommonFriends(id, friendId);
    }
}
