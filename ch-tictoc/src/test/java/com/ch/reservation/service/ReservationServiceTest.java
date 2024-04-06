package com.ch.reservation.service;

import com.ch.core.model.code.Errors;
import com.ch.core.exception.BusinessException;
import com.ch.course.model.StoreCourse;
import com.ch.course.repository.StoreCourseRepository;
import com.ch.global.TestConfig;
import com.ch.global.TestReservationData;
import com.ch.reservation.service.impl.ReservationServiceImpl;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.reservation.dto.response.ReservationResponse;
import com.ch.reservation.repository.ReservationRepository;
import com.ch.user.model.User;
import com.ch.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class ReservationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreCourseRepository storeCourseRepository;
    @Mock
    private ReservationRepository reservationRepository;
    private ReservationServiceImpl reservationService;
    private AutoCloseable closeable;

    private User user;
    private StoreCourse storeCourse;
    private Reservation reservation;
    private ReservationRequest.Create createRequest;
    private ReservationRequest.Cancel cancelRequest;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        reservationService = new ReservationServiceImpl(userRepository, storeCourseRepository, reservationRepository);
        // 사용자 정보
        user = TestReservationData.createSampleUserData();
        // 수업 정보
        storeCourse = TestReservationData.createSampleStoreCourseData(TestReservationData.createSampleStoreData(), TestReservationData.createSampleCourseData());
        // 예약 정보
        reservation = TestReservationData.createSampleReservationData(user, storeCourse);
        // 생성 요청
        createRequest = TestReservationData.createSampleCreateData(storeCourse);
        // 취소 요청
        cancelRequest = TestReservationData.createSampleCancelData(reservation);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @DisplayName("Create Reservation Test - Success")
    @Test
    void createReservation_success() {

        // given
        // Mock userRepository.findById()
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        // Mock storeCourseRepository.findById()
        when(storeCourseRepository.findById(anyLong())).thenReturn(Optional.of(storeCourse));
        // Mock reservationRepository.save()
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // when
        ReservationResponse response = reservationService.createReservation(user.getId(), createRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertNotNull(response.getReservationId());
        assertEquals(ReservationStatus.RESERVE, response.getReservationStatus());

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @DisplayName("Create Reservation Test - Fail (Not Found User)")
    @Test
    void createReservation_fail_userNotFound() {

        // given
        // Mock userRepository.findById()
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.createReservation(1L, createRequest);
        });

        // then
        assertEquals(Errors.NOT_FOUND_USER.getMessage(), exception.getMessage());
    }

    @DisplayName("Create Reservation Test - Fail (Not Found StoreCourse)")
    @Test
    void createReservation_fail_storeCourseNotFound() {

        // given
        // Mock userRepository.findById()
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        // Mock storeCourseRepository.findById()
        when(storeCourseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.createReservation(1L, createRequest);
        });

        // then
        assertEquals(Errors.NOT_FOUND_STORE_COURSE.getMessage(), exception.getMessage());
    }

    @DisplayName("Cancel Reservation Test - Success")
    @Test
    void cancelReservation_success() {

        // given
        Reservation reservation = mock(Reservation.class);
        // Mock reservationRepository.findByUserIdAndIdAndReservationStatus()
        when(reservationRepository.findByUserIdAndIdAndReservationStatus(anyLong(), anyLong(),
                eq(ReservationStatus.RESERVE)))
                .thenReturn(Optional.of(reservation));

        // when
        ReservationResponse response = reservationService.cancelReservation(user.getId(), cancelRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(cancelRequest.getReservationId(), response.getReservationId());
        assertEquals(ReservationStatus.CANCEL, response.getReservationStatus());

        verify(reservation).cancel();
    }

    @DisplayName("Cancel Reservation Test - Fail (Not Found Reservation)")
    @Test
    void cancelReservation_fail_reservationNotFound() {

        // given
        // Mock reservationRepository.findByIdAndUserIdAndReservationStatus()
        when(reservationRepository.findByUserIdAndIdAndReservationStatus(anyLong(), anyLong(), any(ReservationStatus.class)))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationService.cancelReservation(1L, cancelRequest);
        });

        // then
        assertEquals(Errors.NOT_FOUND_RESERVATION_INFO.getMessage(), exception.getMessage());
    }

}