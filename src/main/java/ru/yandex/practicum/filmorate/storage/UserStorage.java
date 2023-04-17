package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(User user);

    void remove(User user);

    User getById(Integer id);

    List<User> getAll();

    List<User> getFriends(Integer userId);

    void sendFriendRequest(Integer userSenderId, Integer userRecipientId);

    void removeFriend(Integer userId, Integer userFriendId);

    List<User> getCommonFriends(Integer userId, Integer anotherUserId);

}
