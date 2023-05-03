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
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

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
        String sqlQuery = "SELECT us.id as id, us.email, us.login, us.name, us.birthday FROM " +
                "(SELECT (user_sender_id + user_recipient_id - " + userId + ") as id, " +
                "FROM friendships WHERE " +
                "(user_recipient_id = " + userId + " AND status=true) OR " +
                "user_sender_id = " + userId +
                ") as fr, users as us WHERE us.id = fr.id";
        return jdbcTemplate.query(sqlQuery, USER_ROW_MAPPER);
    }

    public void sendFriendRequest(Integer userSenderId, Integer userRecipientId) {
        if (Objects.equals(userSenderId, userRecipientId) || userRecipientId < 1) {
            throw new ElementNotFoundException("Unable to process request for identical or negative user ids.");
        }
        String sqlQuery = "INSERT INTO friendships (user_sender_id, user_recipient_id, status) " +
                "VALUES (?, ?, 'FALSE')";

        int rowsUpdated = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setInt(1, userSenderId);
            stmt.setInt(2, userRecipientId);
            return stmt;
        });

        if (rowsUpdated == 0) {
            throw new ElementNotFoundException("Unable to remove user.");
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer userFriendId) {
        String sqlQuery = "DELETE FROM friendships WHERE (user_sender_id = " + userId + " and user_recipient_id = " + userFriendId + ") " +
                "OR (user_recipient_id = " + userId + " and user_sender_id = " + userFriendId + ")";

        jdbcTemplate.update(connection -> {
            return connection.prepareStatement(sqlQuery);
        });
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer anotherUserId) {
        String sqlQuery = "SELECT us.id as id, us.email, us.login, us.name, us.birthday FROM " +
                "(SELECT fr1.id as id FROM " +
                "(SELECT (user_sender_id + user_recipient_id - " + userId + ") as id " +
                "FROM friendships WHERE " +
                "(user_recipient_id = " + userId + " AND status=true) OR " +
                "user_sender_id = " + userId +
                ") as fr1 " +

                "INNER JOIN " +

                "(SELECT (user_sender_id + user_recipient_id - " + anotherUserId + ") as id " +
                "FROM friendships WHERE " +
                "(user_recipient_id = " + anotherUserId + " AND status=true) OR " +
                "user_sender_id = " + anotherUserId +
                ") as fr2 " +

                "ON fr1.id = fr2.id ) as fr," +
                "users as us WHERE us.id = fr.id";

        return jdbcTemplate.query(sqlQuery, USER_ROW_MAPPER);
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
