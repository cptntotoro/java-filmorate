package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;

    @BeforeEach
    public void setup(){ userController = new UserController();
    }

    @Test
    public void add() {
        User userValid = new User("userValid@ya.ru", "userValidLogin", "userValidName", LocalDate.of(1974, 3, 15));
        userController.add(userValid);
        assertTrue(userController.getAll().contains(userValid));
        assertEquals(1, userValid.getId());

        User userInvalidLogin = new User("userInvalidLogin@ya.ru", "userInvalid Login", "userInvalidLoginName", LocalDate.of(1974, 3, 15));
        assertThrows(
                ValidationException.class,
                () -> userController.add(userInvalidLogin),
                "Login must not be empty or contain backspaces.");

        User userInvalidEmail = new User("userInvalidEmail.ya.ru", "userInvalidEmailLogin", "userInvalidEmailName", LocalDate.of(1974, 3, 15));
        assertThrows(
                ValidationException.class,
                () -> userController.add(userInvalidEmail),
                "Email address must not be empty and must contain the '@' symbol.");

        User userInvalidBirthday = new User("userInvalidBirthday@ya.ru", "userInvalidBirthdayLogin", "userInvalidBirthdayName", LocalDate.of(2030, 3, 15));
        assertThrows(
                ValidationException.class,
                () -> userController.add(userInvalidBirthday),
                "Birthday can't set to a future date.");

        User userInvalidName = new User("userInvalidName@ya.ru", "userInvalidNameLogin", "", LocalDate.of(1974, 3, 15));
        userController.add(userInvalidName);
        for (User user : userController.getAll()) {
            if (userInvalidName.equals(user)) {
                assertEquals(user.getLogin(), user.getName());
            }
        }
    }

    @Test
    public void update() {
        User userNew = new User("userNew@ya.ru", "userNewLogin", "userNewName", LocalDate.of(1974, 3, 15));
        userController.add(userNew);

        User userUpdate = new User(1,"userUpdate@ya.ru", "userUpdateLogin", "userUpdateName", LocalDate.of(1964, 8, 9));
        userController.update(userUpdate);
        assertTrue(userController.getAll().contains(userUpdate));
        for (User user : userController.getAll()) {
            if (userUpdate.getId() == user.getId()) {
                assertEquals(userUpdate.getEmail(), user.getEmail());
                assertEquals(userUpdate.getLogin(), user.getLogin());
                assertEquals(userUpdate.getName(), user.getName());
                assertEquals(userUpdate.getBirthday(), user.getBirthday());
            }
        }

        User userInvalidIdUpdate = new User(10,"userInvalidUpdate@ya.ru", "userInvalidUpdateLogin", "userInvalidUpdateName", LocalDate.of(1954, 6, 2));
        assertThrows(
                ValidationException.class,
                () -> userController.update(userInvalidIdUpdate),
                "User with this id was not found and could not be updated.");
    }

    @Test
    public void getAll() {
        User user = new User("userValid@ya.ru", "userValidLogin", "userValidName", LocalDate.of(1974, 3, 15));
        userController.add(user);
        assertEquals(1, userController.getAll().size());
    }
}
