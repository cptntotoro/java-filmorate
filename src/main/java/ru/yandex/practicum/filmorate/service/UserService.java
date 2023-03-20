package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getById(id);
        User userFriend = userStorage.getById(friendId);

        if (!user.getFriends().contains(friendId)) {
            user.addFriend(friendId);
            userFriend.addFriend(id);
        } else {
            throw new ElementNotFoundException("You are already friends with this user.");
        }
    }

    public void removeFriend(int id, int friendId) {
        User user = userStorage.getById(id);
        User userFriend = userStorage.getById(friendId);

        if (user.getFriends().contains(friendId) && userFriend.getFriends().contains(id)) {
            user.removeFriend(friendId);
            userFriend.removeFriend(id);
        } else {
            throw new ElementNotFoundException("These users are not friends.");
        }
    }

    public Set<User> getFriends(int id) {
        User user = userStorage.getById(id);
        return user.getFriends().stream()
                .map(idUser -> userStorage.getById(idUser))
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(int id, int friendId) {
        User user = userStorage.getById(id);
        User userFriend = userStorage.getById(friendId);

        return user.getFriends().stream()
                .filter(userFriend.getFriends()::contains)
                .map(idUser -> userStorage.getById(idUser))
                .collect(Collectors.toSet());
    }

    public User add(User user) {
        setNameAsLoginIfMissing(user);
        userStorage.add(user);
        return user;
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public User update(User user) {
        setNameAsLoginIfMissing(user);
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    private void setNameAsLoginIfMissing(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
