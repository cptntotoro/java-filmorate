package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) {
            addGenres(film.getId(), film.getGenres());
        }

        if (film.getDirectors() != null) {
            addDirectors(film.getId(), film.getDirectors());
        }

        return getById(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (film.getGenres() != null) {
            updateFilmGenres(film);
        }

        if (film.getDirectors() != null) {
            updateFilmDirectors(film);
        }

        return getById(film.getId());
    }

    @Override
    public Film getById(int id) {
        try {
            String sqlQuery = "SELECT f.id," +
                    "       f.name," +
                    "       f.description," +
                    "       f.release_date," +
                    "       f.duration," +
                    "       f.mpa_id as mpa_id," +
                    "       m.name as mpa_name " +
                    "FROM films AS f " +
                    "JOIN mpas AS m ON f.mpa_id = m.id " +
                    "WHERE f.id = ?";

            Film film = jdbcTemplate.queryForObject(sqlQuery, FILM_ROW_MAPPER, id);

            if (film != null) {
                film.setGenres(getFilmGenres(id));
                film.setWhoLiked(getFilmLikes(id));
                film.setDirectors(getFilmDirectors(id));
            }

            return film;

        } catch (EmptyResultDataAccessException ex) {
            throw new ElementNotFoundException("Failed to get film by id = " + id);
        }
    }

    @Override
    public void remove(Film film) {
        int rowsUpdated = jdbcTemplate.update("DELETE FROM films WHERE id = ?", film.getId());

        if (rowsUpdated == 0) {
            throw new ElementNotFoundException("Unable to remove by id = " + film.getId());
        }
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.id as id, f.name as name, f.description as description, f.duration as duration, " +
                "f.release_date as release_date, f.mpa_id as mpa_id, m.name as mpa_name " +
                "FROM films AS f, mpas AS m " +
                "WHERE f.mpa_id = m.id";

        List<Film> films = jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER);

        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
            film.setWhoLiked(getFilmLikes(film.getId()));
            film.setDirectors(getFilmDirectors(film.getId()));
        });

        return films;
    }

    @Override
    public List<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = "SELECT g.id as id, g.name as name " +
                "FROM genres AS g " +
                "INNER JOIN film_genres AS fg ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ? ORDER BY g.id";

        return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER, filmId);
    }

    private void deleteFilmGenres(Integer filmId) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
    }

    public Film updateFilmGenres(Film film) {
        deleteFilmGenres(film.getId());
        addGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public void addLike(Integer userId, Integer filmId) {
        jdbcTemplate.update("INSERT INTO film_likes(user_id, film_id) VALUES (?, ?)", userId, filmId);
    }

    @Override
    public void removeLike(Integer userId, Integer filmId) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE user_id = ? and film_id = ?", userId, filmId);
    }

    @Override
    public List<Film> getTopLikedFilms(Integer count) {

        if (count != null && count < 0) {
            throw new ElementNotFoundException("Negative value count is not allowed.");
        }

        String sqlQuery = "SELECT f.id as id, f.name as name, f.description as description, f.duration as duration, " +
                "f.release_date as release_date, f.mpa_id as mpa_id, m.name as mpa_name, count(fl.user_Id) as likes_count " +
                "FROM films AS f " +
                "LEFT JOIN mpas AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_likes as fl " +
                "ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY likes_count DESC ";

        if (count != null && count > 0) {
            sqlQuery += "LIMIT " + count;
        }

        List<Film> films = jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER);

        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
        });

        return films;
    }

    @Override
    public Mpa getMpa(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name FROM mpas WHERE id = ?", MPA_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ElementNotFoundException("Failed to get mpa by id = " + id);
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT id, name FROM mpas", MPA_ROW_MAPPER);
    }

    @Override
    public Genre getGenre(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name FROM genres WHERE id = ?", GENRE_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ElementNotFoundException("Failed to get film by id = " + id);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT id, name FROM genres", GENRE_ROW_MAPPER);
    }

    private void addGenres(Integer filmId, List<Genre> genres) {
        genres = genres.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(Genre::getId))), ArrayList::new));

        genres.forEach(genre -> {
            jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
        });
    }

    @Override
    public List<Film> getByDirectorSortedByYear(Integer directorId) {

        String sqlQuery = "SELECT f.id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.mpa_id as mpa_id, " +
                "       m.name as mpa_name " +
                "FROM films f " +
                "JOIN mpas AS m ON f.mpa_id = m.id " +
                "JOIN film_directors AS fd ON fd.film_id = f.id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.release_date ASC";

        List<Film> films = jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER, directorId);

        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
            film.setDirectors(getFilmDirectors(film.getId()));
        });
        return films;
    }

    @Override
    public List<Film> getByDirectorSortedByLikes(Integer directorId) {

        String sqlQuery = "SELECT f.id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.mpa_id as mpa_id, " +
                "       m.name as mpa_name, " +
                "       COUNT(fl.user_id) AS likes " +
                "FROM films f " +
                "JOIN mpas AS m ON f.mpa_id = m.id " +
                "JOIN film_directors AS fd ON fd.film_id = f.id " +
                "LEFT JOIN film_likes AS fl ON fl.film_id = f.id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY likes";

        List<Film> films = jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER, directorId);

        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
            film.setDirectors(getFilmDirectors(film.getId()));
        });

        return films;
    }

    private Set<Integer> getFilmLikes(int id) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt(1), id));
    }

    private void addDirectors(Integer filmId, Set<Director> directors) {
        List<Director> directorList = directors.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(Director::getId))), ArrayList::new));

        String sqlQuery = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

        directorList.forEach(director -> jdbcTemplate.update(sqlQuery, filmId, director.getId()));
    }

    private Set<Director> getFilmDirectors(int id) {
        String sqlQuery = "SELECT fd.director_id AS director_id, d.name AS director_name " +
                "FROM film_directors AS fd " +
                "JOIN directors as d ON fd.director_id = d.id " +
                "WHERE fd.film_id = ? " +
                "ORDER BY d.id ASC ";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, DIRECTOR_ROW_MAPPER, id));
    }

    public void updateFilmDirectors(Film film) {
        deleteFilmDirectors(film.getId());
        addDirectors(film.getId(), film.getDirectors());
    }

    private void deleteFilmDirectors(Integer filmId) {
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", filmId);
    }


    private static final RowMapper<Film> FILM_ROW_MAPPER = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        return film;
    };
    private static final RowMapper<Genre> GENRE_ROW_MAPPER = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    };
    private static final RowMapper<Mpa> MPA_ROW_MAPPER = (rs, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    };

    private static final RowMapper<Director> DIRECTOR_ROW_MAPPER = (rs, rowNum) -> {
        Director director = new Director();
        director.setId(rs.getInt("director_id"));
        director.setName(rs.getString("director_name"));
        return director;
    };
}
