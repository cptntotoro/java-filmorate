package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DBUserControllerTest {
    private final UserDbStorage userStorage;
    private User user;

    @Test
    public void add() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        Optional<User> userOptional = Optional.ofNullable(userStorage.add(user));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void getById() {
        assertThrows(ElementNotFoundException.class, () -> userStorage.getById(999));

        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void update() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        User userUpdate = new User(1, "userUpdate@ya.ru", "userUpdateLogin", "userUpdateName",
                LocalDate.of(1960, 5, 19));
        Optional<User> userOptional = Optional.ofNullable(userStorage.update(userUpdate));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1).hasFieldOrPropertyWithValue("email", "userUpdate@ya.ru")
                                .hasFieldOrPropertyWithValue("login", "userUpdateLogin").hasFieldOrPropertyWithValue("name", "userUpdateName")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1960, 5, 19))
                );
    }

    @Test
    public void remove() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));

        assertThrows(ElementNotFoundException.class, () -> userStorage.remove(user));

        User userReturned = userStorage.add(user);
        userStorage.remove(userReturned);

        assertThrows(ElementNotFoundException.class, () -> userStorage.getById(1));
    }

    @Test
    public void getAll() {
        assertEquals(userStorage.getAll().size(), 0);

        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        User user2 = new User("user2@ya.ru", "user2Login", "user2Name", LocalDate.of(1988, 8, 5));
        userStorage.add(user2);

        assertEquals(userStorage.getAll().size(), 2);
    }

    @Test
    public void sendFriendRequest() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        User user2 = new User("user2@ya.ru", "user2Login", "user2Name", LocalDate.of(1988, 8, 5));
        userStorage.add(user2);

        assertEquals(userStorage.getFriends(user.getId()).size(), 0);

        userStorage.sendFriendRequest(user.getId(), user2.getId());

        assertEquals(userStorage.getFriends(user.getId()).size(), 1);

        assertThrows(ElementNotFoundException.class, () -> userStorage.sendFriendRequest(user.getId(), -1));
    }

    @Test
    public void getFriends() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        User user2 = new User("user2@ya.ru", "user2Login", "user2Name", LocalDate.of(1988, 8, 5));
        userStorage.add(user2);

        User user3 = new User("user3@ya.ru", "user3Login", "user3Name", LocalDate.of(1978, 5, 13));
        userStorage.add(user3);

        assertEquals(userStorage.getFriends(user.getId()).size(), 0);

        userStorage.sendFriendRequest(user.getId(), user2.getId());

        assertEquals(userStorage.getFriends(user.getId()).size(), 1);

        userStorage.sendFriendRequest(user.getId(), user3.getId());

        assertEquals(userStorage.getFriends(user.getId()).size(), 2);
    }

    @Test
    public void removeFriend() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        User user2 = new User("user2@ya.ru", "user2Login", "user2Name", LocalDate.of(1988, 8, 5));
        userStorage.add(user2);

        assertEquals(userStorage.getFriends(user.getId()).size(), 0);

        userStorage.sendFriendRequest(user.getId(), user2.getId());

        assertEquals(userStorage.getFriends(user.getId()).size(), 1);

        userStorage.removeFriend(user.getId(), user2.getId());

        assertEquals(userStorage.getFriends(user.getId()).size(), 0);
    }

    @Test
    public void getCommonFriends() {
        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userStorage.add(user);

        User user2 = new User("user2@ya.ru", "user2Login", "user2Name", LocalDate.of(1988, 8, 5));
        userStorage.add(user2);

        User user3 = new User("user3@ya.ru", "user3Login", "user3Name", LocalDate.of(1978, 5, 13));
        userStorage.add(user3);

        assertEquals(userStorage.getFriends(user.getId()).size(), 0);

        userStorage.sendFriendRequest(user.getId(), user2.getId());

        assertEquals(userStorage.getFriends(user.getId()).size(), 1);

        userStorage.sendFriendRequest(user3.getId(), user2.getId());

        assertEquals(userStorage.getFriends(user3.getId()).size(), 1);

        List<User> commonFriends = userStorage.getCommonFriends(user.getId(), user3.getId());

        assertEquals(commonFriends.size(), 1);

        assertThat(Optional.of(commonFriends.get(0)))
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2)
                );
    }

}