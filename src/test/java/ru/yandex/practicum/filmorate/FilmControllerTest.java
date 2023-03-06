package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;

public class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    public void setup(){
        filmController = new FilmController();
    }

    @Test
    public void add() {
        Film filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women...",
                LocalDate.of(1959, 3, 19), 121);
        filmController.add(filmValid);
        assertTrue(filmController.getAll().contains(filmValid));
        assertEquals(1, filmValid.getId());
    }

    @Test
    public void update() {
        Film filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women...",
                LocalDate.of(1959, 3, 19), 121);
        filmController.add(filmValid);

        Film filmValidUpdate = new Film(1,"Valid Film Update", "Valid Film Description",
                LocalDate.of(1959, 3, 19), 121);
        filmController.update(filmValidUpdate);
        assertTrue(filmController.getAll().contains(filmValidUpdate));


        Film filmInvalidUpdate = new Film(10,"Invalid Film Update", "Invalid Film Description",
                LocalDate.of(1959, 3, 19), 121);
        assertThrows(
                ValidationException.class,
                () -> filmController.update(filmInvalidUpdate),
                "Film was not found and can not be updated.");
    }

    @Test
    public void getAll() {
        Film filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women...",
                LocalDate.of(1959, 3, 19), 121);
        filmController.add(filmValid);

        assertEquals(1, filmController.getAll().size());
    }
}
