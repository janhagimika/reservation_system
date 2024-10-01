package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ServRepository servRepository;

    public ReservationRepository(JdbcTemplate jdbcTemplate, UserRepository userRepository, ServRepository servRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.servRepository = servRepository;
    }

    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return Optional.of(mapRowToReservation(rs, rs.getRow()));
            } else {
                return Optional.empty();
            }
        });
    }

    public List<Reservation> findByUserId(Long userId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, new Object[]{userId}, this::mapRowToReservation);
    }

    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        return jdbcTemplate.query(sql, this::mapRowToReservation);
    }

    public int save(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, service_id, start_time, end_time) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, reservation.getUser().getId(), reservation.getService().getServiceId(), reservation.getStartTime(), reservation.getEndTime());
    }

    public int update(Reservation reservation) {
        String sql = "UPDATE reservations SET user_id = ?, service_id = ?, reservation_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, reservation.getUser().getId(), reservation.getService().getServiceId(), reservation.getStartTime(), reservation.getEndTime(), reservation.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    private Reservation mapRowToReservation(ResultSet rs, int rowNum) throws SQLException {
        User user = userRepository.findById(rs.getLong("user_id")).orElse(null);
        Serv service = servRepository.findById(rs.getLong("service_id")).orElse(null);
        LocalDateTime startTime = rs.getObject("start_time", LocalDateTime.class);
        LocalDateTime endTime = rs.getObject("end_time", LocalDateTime.class);
        return new Reservation(rs.getLong("id"), user, service, startTime, endTime);
    }

    public boolean isServiceAvailable(Long serviceId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE service_id = ? " +
                "AND ((? < end_time) AND (? > start_time))";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{serviceId, startTime, endTime}, Integer.class);
        return count == 0; // Pokud neexistuje žádná kolidující rezervace, služba je dostupná
    }

    public boolean hasOverlappingReservation(Long userId, Long serviceId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE user_id = ? AND service_id = ? " +
                "AND ((start_time < ? AND end_time > ?) " +  // Check for overlapping
                "OR (start_time < ? AND end_time > ?))";

        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{
                userId, serviceId, startTime, endTime, endTime, startTime
        }, Integer.class);

        return count != null && count > 0;
    }

    public int deleteAllReservations() {
        String sql = "DELETE * FROM reservations";
        return jdbcTemplate.update(sql);
    }

    public List<Reservation> findReservationsByServiceAndDateRange(Long serviceId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM reservations WHERE service_id = ? " +
                "AND (start_time BETWEEN ? AND ? OR end_time BETWEEN ? AND ?)";

        // Fetch the service entity using its ID
        Optional<Serv> optionalService = servRepository.findById(serviceId);
        if (!optionalService.isPresent()) {
            throw new RuntimeException("Service not found for ID: " + serviceId);
        }
        Serv service = optionalService.get();

        return jdbcTemplate.query(sql, new Object[]{serviceId, startDate, endDate, startDate, endDate},
                (rs, rowNum) -> {
                    // Fetch user from the user repository
                    Optional<User> optionalUser = userRepository.findById(rs.getLong("user_id"));
                    if (!optionalUser.isPresent()) {
                        throw new RuntimeException("User not found for ID: " + rs.getLong("user_id"));
                    }
                    User user = optionalUser.get();

                    return new Reservation(
                            rs.getLong("id"),
                            user,
                            service,
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            rs.getTimestamp("end_time").toLocalDateTime()
                    );
                }
        );
    }

}
