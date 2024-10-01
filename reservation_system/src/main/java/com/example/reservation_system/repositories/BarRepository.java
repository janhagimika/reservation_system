package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Bar;
import com.example.reservation_system.models.Serv;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BarRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ServRepository servRepository;

    public BarRepository(JdbcTemplate jdbcTemplate, ServRepository servRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.servRepository = servRepository;
    }

    public List<Bar> findAll() {
        String sql = "SELECT * FROM bars";
        return jdbcTemplate.query(sql, this::mapRowToBar);
    }

    public Optional<Bar> findById(Long id) {
        String sql = "SELECT * FROM bars WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToBar(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public Optional<Bar> findByServiceId(Long serviceId) {
        String sql = "SELECT * FROM bars WHERE service_id = ?";
        return jdbcTemplate.query(sql, new Object[]{serviceId}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToBar(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public int save(Bar bar) {
        String sql = "INSERT INTO bars (name, service_id, cuisine_type, capacity, location) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, bar.getServiceName(), bar.getService().getServiceId(), bar.getCuisineType(), bar.getCapacity(), bar.getLocation());
    }

    public int update(Bar bar) {
        String sql = "UPDATE bars SET name = ?, service_id = ?, cuisine_type = ?, capacity = ?, location = ? WHERE id = ?";
        return jdbcTemplate.update(sql, bar.getServiceName(), bar.getService().getServiceId(), bar.getCuisineType(), bar.getCapacity(), bar.getLocation(), bar.getServiceId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM bars WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Bar mapRowToBar(ResultSet rs, int rowNum) throws SQLException {
        Serv service = servRepository.findById(rs.getLong("service_id")).orElse(null);
        return new Bar(rs.getLong("id"), rs.getString("name"), service, rs.getString("cuisine_type"), rs.getInt("capacity"), rs.getString("location"));
    }

    public int deleteAllBars() {
        String sql = "DELETE * FROM bars";
        return jdbcTemplate.update(sql);
    }
}
