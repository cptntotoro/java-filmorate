package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FunctionalityNotImplemetedException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
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
    public void remove(User user) {
        throw new FunctionalityNotImplemetedException("This functionality has not been implemented.");
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

    @Override
    public List<User> getFriends(Integer id) {
        User user = getById(id);
        return user.getFriends().stream()
                .map(idUser -> getById(idUser))
                .collect(Collectors.toList());
    }

    @Override
    public void sendFriendRequest(Integer userSenderId, Integer userRecipientId) {
        User userSender = getById(userSenderId);
        User userRecipient = getById(userRecipientId);

        if (!userSender.getFriends().contains(userRecipientId)) {
            userSender.addFriend(userRecipientId);
            userRecipient.addFriend(userSenderId);
        } else {
            throw new ElementNotFoundException("You are already friends with this user.");
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer userFriendId) {
        User user = getById(userId);
        User userFriend = getById(userFriendId);

        if (user.getFriends().contains(userFriendId) && userFriend.getFriends().contains(userId)) {
            user.removeFriend(userFriendId);
            userFriend.removeFriend(userId);
        } else {
            throw new ElementNotFoundException("These users are not friends.");
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer anotherUserId) {
        User user = getById(userId);
        User userFriend = getById(anotherUserId);

        return user.getFriends().stream()
                .filter(userFriend.getFriends()::contains)
                .map(idUser -> getById(idUser))
                .collect(Collectors.toList());
    }

}
