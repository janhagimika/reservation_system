package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Serv;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ServRepository {

    private final JdbcTemplate jdbcTemplate;

    public ServRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Serv> findById(Long id) {
        String sql = "SELECT * FROM services WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToServ(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public List<Serv> findAll() {
        String sql = "SELECT * FROM services";
        return jdbcTemplate.query(sql, this::mapRowToServ);
    }

    public Serv save(Serv serv) {
        String sql = "INSERT INTO services (service_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setString(1, serv.getServiceName());
            return ps;
        }, keyHolder);
        serv.setServiceId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return serv;
    }

    public int update(Serv serv) {
        String sql = "UPDATE services SET service_name = ?, service_type = ? WHERE id = ?";
        return jdbcTemplate.update(sql, serv.getServiceName(), serv.getServiceId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM services WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Serv mapRowToServ(ResultSet rs, int rowNum) throws SQLException {
        Serv serv = new Serv(rs.getLong("id"),
                rs.getString("service_name")
        );
        return serv;
    }

    public int deleteAllServs() {
        String sql = "DELETE * FROM services";
        return jdbcTemplate.update(sql);
    }
}
