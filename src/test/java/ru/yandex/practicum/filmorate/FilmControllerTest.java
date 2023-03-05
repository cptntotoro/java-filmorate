package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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


        Film filmInvalidName = new Film("", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women...",
                LocalDate.of(1959, 3, 19), 121);
        assertThrows(
                ValidationException.class,
                () -> filmController.add(filmInvalidName),
                "Film validation failed.");


        Film filmInvalidDescription = new Film("Some Like It Hot", "After two Chicago musicians, " +
                "Joe and Jerry, witness the the St. Valentine's Day massacre, they want to get out of town and " +
                "get away from the gangster responsible, Spats Colombo. They're desperate to get a gig out of town but...",
                LocalDate.of(1959, 3, 19), 121);
        assertThrows(
                ValidationException.class,
                () -> filmController.add(filmInvalidDescription),
                "Film validation failed.");


        Film filmInvalidReleaseDate = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women...",
                LocalDate.of(1813, 3, 19), 121);
        assertThrows(
                ValidationException.class,
                () -> filmController.add(filmInvalidReleaseDate));


        Film filmInvalidDuration = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women...",
                LocalDate.of(1959, 3, 19), -5);
        assertThrows(
                ValidationException.class,
                () -> filmController.add(filmInvalidDuration),
                "Film validation failed.");
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
