package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Lift;
import com.example.reservation_system.models.Serv;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class LiftRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ServRepository servRepository;

    public LiftRepository(JdbcTemplate jdbcTemplate, ServRepository servRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.servRepository = servRepository;
    }

    public List<Lift> findAll() {
        String sql = "SELECT * FROM lifts";
        return jdbcTemplate.query(sql, this::mapRowToLift);
    }

    public Optional<Lift> findById(Long id) {
        String sql = "SELECT * FROM lifts WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToLift(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public Optional<Lift> findByServiceId(Long serviceId) {
        String sql = "SELECT * FROM lifts WHERE service_id = ?";
        return jdbcTemplate.query(sql, new Object[]{serviceId}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToLift(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public int save(Lift lift) {
        String sql = "INSERT INTO lifts (name, service_id, capacity, status) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, lift.getServiceName(), lift.getService().getServiceId(), lift.getCapacity(), lift.getStatus());
    }

    public int update(Lift lift) {
        String sql = "UPDATE lifts SET name = ?, service_id = ?, capacity = ?, status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, lift.getServiceName(), lift.getService().getServiceId(), lift.getCapacity(), lift.getStatus(), lift.getServiceId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM lifts WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Lift mapRowToLift(ResultSet rs, int rowNum) throws SQLException {
        Serv service = servRepository.findById(rs.getLong("service_id")).orElse(null);
        return new Lift(rs.getLong("id"), rs.getString("name"), service, rs.getInt("capacity"), rs.getString("status"));
    }

    public int deleteAllLifts() {
        String sql = "DELETE * FROM lifts";
        return jdbcTemplate.update(sql);
    }
}
