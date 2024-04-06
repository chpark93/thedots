package com.ch.reservation.repository;

import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserIdAndStoreCourseIdAndReservationStatus(Long userId, Long storeCourseId, ReservationStatus reservationStatus);
    Optional<Reservation> findByUserIdAndIdAndReservationStatus(Long userId, Long reservationId, ReservationStatus reservationStatus);
    Optional<List<Reservation>> findAllByStoreCourseIdAndReservationDateAndReservationStatus(Long storeCourseId, LocalDate date, ReservationStatus reservationStatus);

}
