package com.example.reservation_system.services;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReservationTransactionService {

    @Transactional
    public Reservation createReservationInternal(Reservation reservation, User user, Serv service, ReservationRepository reservationRepository) {
        reservation.setUser(user);
        reservation.setService(service);
        return reservationRepository.save(reservation);
    }
}
