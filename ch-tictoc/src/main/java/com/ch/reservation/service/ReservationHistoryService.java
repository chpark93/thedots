package com.ch.reservation.service;

import com.ch.reservation.dto.response.ReservationHistoryResponse;
import org.springframework.data.domain.Pageable;

public interface ReservationHistoryService {

    ReservationHistoryResponse searchReservationsByReserved(Long storeCourseId, Pageable pageable);
    ReservationHistoryResponse searchReservationsByAll(Long storeCourseId, Pageable pageable);

}
