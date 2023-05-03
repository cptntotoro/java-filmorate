package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DBFilmControllerTest {

    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;

    private Film film;

    private User user;

    @Test
    public void add() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.add(film));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void update() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        filmDbStorage.add(film);

        Film film2 = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120, new Mpa(2));
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.update(film2));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Around the World in 80 Days")
                                .hasFieldOrPropertyWithValue("description", "To win a bet, an eccentric British inventor...")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2004, 6, 13))
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .extracting("mpa").hasFieldOrPropertyWithValue("id", 2)
                );
    }

    @Test
    public void remove() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));

        assertThrows(ElementNotFoundException.class, () -> filmDbStorage.remove(film));

        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.add(film));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );

        filmDbStorage.remove(film);

        assertThrows(ElementNotFoundException.class, () -> filmDbStorage.getById(1));
    }

    @Test
    public void getAll() {
        assertEquals(filmDbStorage.getAll().size(), 0);

        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        filmDbStorage.add(film);

        Film film2 = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120, new Mpa(2));
        filmDbStorage.add(film2);

        assertEquals(filmDbStorage.getAll().size(), 2);
    }

    @Test
    public void getById() {
        assertThrows(ElementNotFoundException.class, () -> filmDbStorage.getById(999));

        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        filmDbStorage.add(film);

        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void getFilmGenres() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.add(film));

        Optional<List<Genre>> genreOptional = Optional.ofNullable(filmDbStorage.getFilmGenres(filmOptional.get().getId()));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genres ->
                        assertThat(genres).isEmpty()
                );

        film = filmOptional.get();
        List<Genre> genreList = new ArrayList<>();
        genreList.add(new Genre(1));
        film.setGenres(genreList);
        filmOptional = Optional.ofNullable(filmDbStorage.update(film));

        genreOptional = Optional.ofNullable(filmDbStorage.getFilmGenres(filmOptional.get().getId()));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genres ->
                        assertThat(genres).hasSize(1)
                );
    }

    @Test
    public void updateFilmGenres() {
        List<Genre> initialGenres = List.of(filmDbStorage.getGenre(1), filmDbStorage.getGenre(2));

        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, initialGenres, new Mpa(1));

        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.add(film));
        Optional<List<Genre>> genresOptional = Optional.ofNullable(filmDbStorage.getFilmGenres(filmOptional.get().getId()));

        assertThat(genresOptional)
                .isPresent()
                .hasValueSatisfying(genres ->
                        assertThat(genres).hasSize(2)
                );

        List<Genre> updatedGenres = List.of(filmDbStorage.getGenre(3), filmDbStorage.getGenre(4), filmDbStorage.getGenre(5));

        film = new Film(1, "Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, updatedGenres, new Mpa(1));

        Optional<Film> filmUpdateOptional = Optional.ofNullable(filmDbStorage.updateFilmGenres(film));
        Optional<List<Genre>> genresReceivedOptional = Optional.ofNullable(filmDbStorage.getFilmGenres(filmUpdateOptional.get().getId()));

        assertIterableEquals(genresReceivedOptional.get(), updatedGenres);
    }

    @Test
    public void addLike() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        filmDbStorage.add(film);

        Film film2 = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120, new Mpa(2));
        filmDbStorage.add(film2);

        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userDbStorage.add(user);

        filmDbStorage.addLike(user.getId(), film2.getId());

        List<Film> popularFilms = filmDbStorage.getTopLikedFilms(1);

        assertEquals(popularFilms.size(), 1);
        assertEquals(popularFilms.get(0).getId(), 2);
    }

    @Test
    public void removeLike() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        filmDbStorage.add(film);

        Film film2 = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120, new Mpa(2));
        filmDbStorage.add(film2);

        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userDbStorage.add(user);

        filmDbStorage.addLike(user.getId(), film2.getId());

        List<Film> popularFilms = filmDbStorage.getTopLikedFilms(1);

        assertEquals(popularFilms.size(), 1);
        assertEquals(popularFilms.get(0).getId(), 2);

        filmDbStorage.addLike(user.getId(), film.getId());
        filmDbStorage.removeLike(user.getId(), film2.getId());

        popularFilms = filmDbStorage.getTopLikedFilms(1);

        assertEquals(popularFilms.size(), 1);
        assertEquals(popularFilms.get(0).getId(), 1);
    }

    @Test
    public void getTopLikedFilms() {
        film = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121, new Mpa(1));
        filmDbStorage.add(film);

        Film film2 = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120, new Mpa(2));
        filmDbStorage.add(film2);

        user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        userDbStorage.add(user);

        filmDbStorage.addLike(user.getId(), film2.getId());

        List<Film> popularFilms = filmDbStorage.getTopLikedFilms(1);

        assertEquals(popularFilms.size(), 1);
        assertEquals(popularFilms.get(0).getId(), 2);

        popularFilms = filmDbStorage.getTopLikedFilms(2);

        assertEquals(popularFilms.size(), 2);
        assertEquals(popularFilms.get(0).getId(), 2);
        assertEquals(popularFilms.get(1).getId(), 1);

        assertThrows(ElementNotFoundException.class, () -> filmDbStorage.getTopLikedFilms(-1));
    }

    @Test
    public void getMpa() {
        Optional<Mpa> mpa = Optional.of(filmDbStorage.getMpa(1));
        assertEquals(mpa.get().getName(), "G");

        assertThrows(ElementNotFoundException.class, () -> filmDbStorage.getMpa(-1));
    }

    @Test
    public void getAllMpa() {
        List<Mpa> allMpa = filmDbStorage.getAllMpa();
        assertEquals(allMpa.size(), 5);
    }

    @Test
    public void getGenre() {
        Optional<Genre> genre = Optional.of(filmDbStorage.getGenre(1));
        assertEquals(genre.get().getName(), "Комедия");

        assertThrows(ElementNotFoundException.class, () -> filmDbStorage.getGenre(-1));
    }

    @Test
    public void getAllGenres() {
        List<Genre> allGenres = filmDbStorage.getAllGenres();
        assertEquals(allGenres.size(), 6);
    }

}