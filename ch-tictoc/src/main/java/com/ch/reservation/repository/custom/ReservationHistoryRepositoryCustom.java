package com.ch.reservation.repository.custom;

import com.ch.course.model.StoreCourse;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationHistoryRepositoryCustom {

    Page<Reservation> findDistinctByOrderByReservationDateDesc(StoreCourse storeCourse, ReservationStatus reservationStatus, Pageable pageable);

}
