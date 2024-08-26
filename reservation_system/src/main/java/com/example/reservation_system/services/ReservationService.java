package com.example.reservation_system.services;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.ReservationRepository;
import com.example.reservation_system.repositories.ServRepository;
import com.example.reservation_system.repositories.UserRepository;
import com.example.reservation_system.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final UserRepository userRepository;

    private final ServRepository servRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, ServRepository servRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.servRepository = servRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public int createReservation(Reservation reservation) {
        // Kontrola, zda je uživatel přihlášen a má právo na rezervaci
        if (reservation.getUser() == null) {
            throw new IllegalStateException("Rezervace musí být přidělena přihlášenému uživateli.");
        }

        // Kontrola dostupnosti služby (pokoj, bar, lanovka)
        if (!isServiceAvailable(reservation.getService(), reservation.getStartTime(),reservation.getEndTime())) {
            throw new IllegalStateException("Služba není ve zvoleném čase dostupná.");
        }
        if (reservationRepository.isUserAlreadyBookedForRoom(reservation.getUser().getId(), reservation.getStartTime(), reservation.getEndTime())) {
            throw new IllegalStateException("Tento uživatel už má ve zvoleném čase jinou rezervaci.");
        }

        // Odeslání potvrzovacího e-mailu

        User user = userRepository.findById(reservation.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Uživatel nebyl nalezen."));
        Serv service = servRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> new IllegalArgumentException("Servis nebyl nalezen"));

        reservation.setUser(user);
        reservation.setService(service);

        String emailBody = String.format("Vážený pane/Vážená paní, %s, Vaše rezervace na %s od %s do %s byla právě potvrzena.",
                reservation.getUser().getUsername(),
                reservation.getService().getServiceName(),
                reservation.getStartTime(),
                reservation.getEndTime());
        System.out.println(emailBody);
         //didn't want to register there uselessly
        /*emailService.sendReservationConfirmation(
                reservation.getUser().getEmail(),
                "Reservation Confirmation",
                emailBody
        );*/
        return reservationRepository.save(reservation);
    }
    private boolean isServiceAvailable(Serv service, LocalDateTime startTime,LocalDateTime endTime) {
        return reservationRepository.isServiceAvailable(service.getId(), startTime, endTime);
    }

    public int updateReservation(Long id, Reservation reservation) {
        Optional<Reservation> existingReservation = reservationRepository.findById(id);
        if (existingReservation.isPresent()) {
            reservation.setId(id);
            return reservationRepository.update(reservation);
        } else {
            return 0;
        }
    }

    public int deleteReservation(Long id) {
        return reservationRepository.deleteById(id);
    }
}
