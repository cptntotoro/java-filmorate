package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return getById(user.getId());
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        int rowsUpdated = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setInt(5, user.getId());
            return stmt;
        });
        if (rowsUpdated == 0) {
            throw new ElementNotFoundException("Unable to update user.");
        }
        return getById(user.getId());
    }

    @Override
    public void remove(User user) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, user.getId());
            return stmt;
        });
        if (rowsUpdated == 0) {
            throw new ElementNotFoundException("Unable to remove user.");
        }
    }

    @Override
    public User getById(Integer id) {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, USER_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ElementNotFoundException("Failed to get user by this id.");
        }
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, USER_ROW_MAPPER);
    }

    @Override
    public List<User> getFriends(Integer userId) {
//
        String sqlQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friendships AS fs " +
                "JOIN users as u ON fs.user_recipient_id = u.id " +
                "WHERE fs.user_sender_id = ?";

        return jdbcTemplate.query(sqlQuery, USER_ROW_MAPPER, userId);
    }

    public void sendFriendRequest(Integer userSenderId, Integer userRecipientId) {
        if (Objects.equals(userSenderId, userRecipientId) || userSenderId < 1 || userRecipientId < 1) {
            throw new ElementNotFoundException("Failed to process request for identical, negative or equal to zero user ids.");
        }

        jdbcTemplate.update("INSERT INTO friendships (user_sender_id, user_recipient_id) " +
                "VALUES (?, ?)", userSenderId, userRecipientId);
    }

    @Override
    public void removeFriend(Integer userId, Integer userFriendId) {
        jdbcTemplate.update("DELETE FROM friendships WHERE (user_sender_id = ? AND user_recipient_id = ?) " +
                "OR (user_recipient_id = ? AND user_sender_id = ?)", userId, userFriendId, userId, userFriendId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer anotherUserId) {
        String sqlQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users AS u " +
                "WHERE u.id IN " +
                "(SELECT f1.user_recipient_id " +
                "FROM friendships f1 " +
                "WHERE user_sender_id = ? " +
                "INTERSECT " +
                "SELECT f2.user_recipient_id " +
                "FROM friendships f2 " +
                "WHERE user_sender_id = ?)";

        return jdbcTemplate.query(sqlQuery, USER_ROW_MAPPER, anotherUserId, userId);
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };
}