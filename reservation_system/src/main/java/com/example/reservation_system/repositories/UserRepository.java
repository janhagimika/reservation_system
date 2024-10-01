package com.example.reservation_system.repositories;

import com.example.reservation_system.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.query(sql, new Object[]{username}, rs -> {
            if (rs.next()) {
                return mapRowToUser(rs, rs.getRow());
            } else {
                return null;  // Return null if no user is found
            }
        });
    }
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, new Object[]{email}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToUser(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id ASC";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }
    public int save(User user) {
        String sql = "INSERT INTO users (username, password, email, role, first_name, surname, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), user.getFirstName(), user.getSurname(), user.getPhoneNumber());
    }

    public int update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, first_name = ?, surname = ?, email = ?, role = ?, phone_number = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getFirstName(), user.getSurname(), user.getEmail(), user.getRole(), user.getPhoneNumber(), user.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, newPassword, userId);
    }


    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getString("first_name"),
                rs.getString("surname"),
                rs.getString("phone_number")
        );
        return user;
    }

    public int deleteAllUsers() {
        String sql = "DELETE * FROM users";
        return jdbcTemplate.update(sql);
    }
}
