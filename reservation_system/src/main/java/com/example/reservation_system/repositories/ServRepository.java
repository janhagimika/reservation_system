package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Serv;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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

    public int save(Serv serv) {
        String sql = "INSERT INTO services (service_name) VALUES (?)";
        return jdbcTemplate.update(sql, serv.getServiceName());
    }

    public int update(Serv serv) {
        String sql = "UPDATE services SET service_name = ? WHERE id = ?";
        return jdbcTemplate.update(sql, serv.getServiceName(), serv.getId());
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
}
