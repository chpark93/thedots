package com.ch.reservation.repository;

import com.ch.reservation.model.Reservation;
import com.ch.reservation.repository.custom.ReservationHistoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationHistoryRepository extends JpaRepository<Reservation, Long>, ReservationHistoryRepositoryCustom {

}
