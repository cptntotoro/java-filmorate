package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.Director;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return get(director.getId());
    }

    @Override
    public Director get(int id) {
        try {
            String sqlQuery = "SELECT id, name FROM directors WHERE id = ?";

            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> new Director(
                    rs.getInt(1),
                    rs.getString(2)
            ), id);

        } catch (EmptyResultDataAccessException ex) {
            throw new ElementNotFoundException("Failed to get director by id " + id);
        }
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return get(director.getId());
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT id, name FROM directors";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        });
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            return stmt;
        });
    }
}
