package com.ch.reservation.service.impl;

import com.ch.core.annotation.DistributeLock;
import com.ch.core.model.code.Errors;
import com.ch.core.exception.BusinessException;
import com.ch.course.model.StoreCourse;
import com.ch.course.repository.StoreCourseRepository;
import com.ch.reservation.service.ReservationService;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.reservation.dto.response.ReservationResponse;
import com.ch.reservation.repository.ReservationRepository;
import com.ch.user.model.User;
import com.ch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final UserRepository userRepository;
    private final StoreCourseRepository storeCourseRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 수업 예약
     * @param userId 사용자 고유 ID
     * @param reservationRequest 수업 예약 RequestBody
     * @return ReservationResponse
     */
    @Override
    @DistributeLock(value= "#userId", lockName = "reservationLock") // TransactionAspect로 Transaction 적용
    public ReservationResponse createReservation(Long userId, ReservationRequest.Create reservationRequest) {

        // 동일 매장, 동일 수업 중복 예약 체크
        // 예약 취소 후에는 재등록 가능
        if ( reservationRepository.existsByUserIdAndStoreCourseIdAndReservationStatus(
                userId, reservationRequest.getStoreCourseId(), ReservationStatus.RESERVE) ) {
            throw new BusinessException(Errors.DUPLICATED_USER_AND_COURSE.getMessage(), Errors.DUPLICATED_USER_AND_COURSE);
        }
        // 예약자 정보 조회
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(Errors.NOT_FOUND_USER.getMessage(), Errors.NOT_FOUND_USER)
        );
        // 매장-수업 정보 조회
        StoreCourse storeCourse = storeCourseRepository.findById(reservationRequest.getStoreCourseId()).orElseThrow(
                () -> new BusinessException(Errors.NOT_FOUND_STORE_COURSE.getMessage(), Errors.NOT_FOUND_STORE_COURSE)
        );

        // 예약시 Count 처리 및 예외 처리
        int reservationCount = calculateReservationCount(reservationRequest);
        int remainCount = calculateRemainCount(storeCourse.getCourse().getCount(), reservationCount, reservationRequest.getCount());
        if ( remainCount < 0 ) {
            throw new BusinessException(Errors.NOT_POSSIBLE_RESERVATION.getMessage(), Errors.NOT_POSSIBLE_RESERVATION);
        }

        // Create Reservation
        return ReservationResponse.builder()
                .userId(userId)
                .reservationId(
                        reservationRepository.save(Reservation.builder()
                                .user(user)
                                .storeCourse(storeCourse)
                                .count(reservationRequest.getCount())
                                .reservationDate(reservationRequest.getReservationDate())
                                .reservationStatus(ReservationStatus.RESERVE)
                                .build()
                        ).getId()
                )
                .reservationStatus(ReservationStatus.RESERVE)
                .build();
    }

    /**
     * 수업 예약 취소
     * @param userId 사용자 고유 ID
     * @param reservationRequest 수업 예약 취소 RequestBody
     * @return ReservationResponse
     */
    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long userId, ReservationRequest.Cancel reservationRequest) {

        // 해당 Reservation 정보 조회
        Reservation reservation = reservationRepository
                .findByUserIdAndIdAndReservationStatus(userId, reservationRequest.getReservationId(), ReservationStatus.RESERVE).orElseThrow(
                () -> new BusinessException(Errors.NOT_FOUND_RESERVATION_INFO.getMessage(), Errors.NOT_FOUND_RESERVATION_INFO)
        );

        // Cancel Reservation
        reservation.cancel();

        return ReservationResponse.builder()
                .userId(userId)
                .reservationId(reservationRequest.getReservationId())
                .reservationStatus(ReservationStatus.CANCEL)
                .build();
    }

    /**
     * 수업-날짜 예약 인원 Count 계산
     * @param reservationRequest 수업 예약 RequestBody
     * @return int
     */
    private int calculateReservationCount(ReservationRequest.Create reservationRequest) {
        return reservationRepository.findAllByStoreCourseIdAndReservationDateAndReservationStatus(
                        reservationRequest.getStoreCourseId(), reservationRequest.getReservationDate(), ReservationStatus.RESERVE)
                .map(reservations -> reservations.stream()
                        .mapToInt(Reservation::getCount)
                        .sum())
                .orElse(0); // 예약이 없는 경우 0 반환
    }

    /**
     * 예약 가능 Count 계산
     * @param totalCourseCount 수업 예약 최대 인원
     * @param reservationCount 수업-날짜 예약중 인원 
     * @param reservationRequestCount 해당 예약자 요청 인원
     * @return int
     */
    private int calculateRemainCount(int totalCourseCount, int reservationCount, int reservationRequestCount) {
        return totalCourseCount - (reservationCount + reservationRequestCount);
    }


}
