package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Room;
import com.example.reservation_system.models.Serv;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ServRepository servRepository;

    public RoomRepository(JdbcTemplate jdbcTemplate, ServRepository servRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.servRepository = servRepository;
    }

    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        return jdbcTemplate.query(sql, this::mapRowToRoom);
    }

    public Optional<Room> findById(Long id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToRoom(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public Optional<Room> findByServiceId(Long serviceId) {
        String sql = "SELECT * FROM rooms WHERE service_id = ?";
        return jdbcTemplate.query(sql, new Object[]{serviceId}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToRoom(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public int save(Room room) {
        String sql = "INSERT INTO rooms (name, service_id, capacity, price_per_night) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, room.getServiceName(), room.getService().getServiceId(), room.getCapacity(), room.getPricePerNight());
    }

    public int update(Room room) {
        String sql = "UPDATE rooms SET name = ?, service_id = ?, capacity = ?, price_per_night = ? WHERE id = ?";
        return jdbcTemplate.update(sql, room.getServiceName(), room.getService().getServiceId(), room.getCapacity(), room.getPricePerNight(), room.getServiceId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Room mapRowToRoom(ResultSet rs, int rowNum) throws SQLException {
        Serv service = servRepository.findById(rs.getLong("service_id")).orElse(null);
        return new Room(rs.getLong("id"), rs.getString("name"),service, rs.getInt("capacity"), rs.getDouble("price_per_night"));
    }

    public int deleteAllRooms() {
        String sql = "DELETE * FROM rooms";
        return jdbcTemplate.update(sql);
    }
}
