package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    public void addFriend(int userSenderId, int userRecipientId) {
        userStorage.sendFriendRequest(userSenderId, userRecipientId);
    }

    public User update(User user) {
        setNameAsLoginIfMissing(user);
        return userStorage.update(user);
    }

    public void removeFriend(int id, int friendId) {
        userStorage.removeFriend(id, friendId);
    }

    public Set<User> getFriends(int id) {
        return new HashSet<>(userStorage.getFriends(id));
    }

    public Set<User> getCommonFriends(int id, int friendId) {
        return new HashSet<>(userStorage.getCommonFriends(id, friendId));
    }

    public User add(User user) {
        setNameAsLoginIfMissing(user);
        userStorage.add(user);
        return user;
    }

    public User getById(int id) {
        return userStorage.getById(id);
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
