package com.ch.reservation.service;

import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.reservation.dto.response.ReservationResponse;

public interface ReservationService {

    ReservationResponse createReservation(Long userId, ReservationRequest.Create reservationRequest);
    ReservationResponse cancelReservation(Long userId, ReservationRequest.Cancel reservationRequest);

}
