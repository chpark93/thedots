package com.ch.reservation.service;

import com.ch.core.model.code.Errors;
import com.ch.core.exception.BusinessException;
import com.ch.course.model.StoreCourse;
import com.ch.course.repository.StoreCourseRepository;
import com.ch.global.TestConfig;
import com.ch.global.TestReservationData;
import com.ch.reservation.service.impl.ReservationHistoryServiceImpl;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.dto.response.ReservationHistoryResponse;
import com.ch.reservation.repository.ReservationHistoryRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class ReservationHistoryServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreCourseRepository storeCourseRepository;
    @Mock
    private ReservationHistoryRepository reservationHistoryRepository;
    private ReservationHistoryServiceImpl reservationHistoryService;
    private AutoCloseable closeable;

    private User user;
    private StoreCourse storeCourse;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        reservationHistoryService = new ReservationHistoryServiceImpl(userRepository, storeCourseRepository, reservationHistoryRepository);
        // 사용자 정보
        user = TestReservationData.createSampleUserData();
        // 수업 정보
        storeCourse = TestReservationData.createSampleStoreCourseData(TestReservationData.createSampleStoreData(), TestReservationData.createSampleCourseData());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @DisplayName("Search Reservations By Reserved Test - Success")
    @Test
    void searchReservationsByReserved_success() {

        // given
        // 예약 정보
        reservation = TestReservationData.createSampleReservationData(user, storeCourse);

        // Mock storeCourseRepository.findById()
        when(storeCourseRepository.findById(any())).thenReturn(Optional.of(storeCourse));
        // Mock reservationRepository.findDistinctByOrderByReservationDateDesc()
        when(reservationHistoryRepository.findDistinctByOrderByReservationDateDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(reservation)));
        // Mock userRepository.findAllById()
        when(userRepository.findAllById(any())).thenReturn(Collections.singletonList(user));

        // when
        ReservationHistoryResponse response = reservationHistoryService.searchReservationsByReserved(1L, PageRequest.of(0, 10));

        // then
        assertEquals(1, response.getContent().size());
        assertEquals(storeCourse.getId(), response.getStoreCourseId());
        assertEquals(storeCourse.getStore().getName(), response.getStoreName());
        assertEquals(storeCourse.getCourse().getName(), response.getCourseName());

        ReservationHistoryResponse.History history = response.getContent().get(0);
        assertEquals(LocalDate.now().plusDays(2), history.getDate());
        assertEquals(3, history.getTotalCount());

        ReservationHistoryResponse.Info info = history.getInfos().get(0);
        assertEquals(user.getId(), info.getUserId());
        assertEquals(user.getName(), info.getUserName());
        assertEquals(user.getEmail(), info.getEmail());
        assertEquals(reservation.getCount(), info.getCount());
        assertEquals(reservation.getReservationStatus(), info.getStatus());
        assertEquals(1, response.getContent().size());
    }

    @DisplayName("Search Reservations By Reserved Test - Fail (Not Found StoreCourse)")
    @Test
    void searchReservationsByReserved_fail_storeCourseNotFound() {

        // given
        when(storeCourseRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationHistoryService.searchReservationsByReserved(1L, PageRequest.of(0, 10));
        });

        // then
        assertEquals(Errors.NOT_FOUND_STORE_COURSE.getMessage(), exception.getMessage());
    }

    @DisplayName("Search Reservations By All Test - Success")
    @Test
    void searchReservationsByAll_success() {

        // given
        // 예약 정보
        reservation = TestReservationData.createSampleReservationCancelData(user, storeCourse);

        // Mock storeCourseRepository.findById()
        when(storeCourseRepository.findById(any())).thenReturn(Optional.of(storeCourse));
        // Mock reservationRepository.findDistinctByOrderByReservationDateDesc()
        when(reservationHistoryRepository.findDistinctByOrderByReservationDateDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(reservation)));
        // Mock userRepository.findAllById()
        when(userRepository.findAllById(any())).thenReturn(Collections.singletonList(user));

        // 메서드 실행
        ReservationHistoryResponse response = reservationHistoryService.searchReservationsByAll(1L, PageRequest.of(0, 10));

        // then
        assertEquals(1, response.getContent().size());
        assertEquals(storeCourse.getId(), response.getStoreCourseId());
        assertEquals(storeCourse.getStore().getName(), response.getStoreName());
        assertEquals(storeCourse.getCourse().getName(), response.getCourseName());

        ReservationHistoryResponse.History history = response.getContent().get(0);
        assertEquals(LocalDate.now().plusDays(2), history.getDate());
        assertEquals(3, history.getTotalCount());

        ReservationHistoryResponse.Info info = history.getInfos().get(0);
        assertEquals(user.getId(), info.getUserId());
        assertEquals(user.getName(), info.getUserName());
        assertEquals(user.getEmail(), info.getEmail());
        assertEquals(reservation.getCount(), info.getCount());
        assertEquals(reservation.getReservationStatus(), info.getStatus());
        assertEquals(1, response.getContent().size());
    }

    @DisplayName("Search Reservations By All Test - Fail (Not Found StoreCourse)")
    @Test
    void searchReservationsByAll_fail_storeCourseNotFound() {

        // given
        when(storeCourseRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservationHistoryService.searchReservationsByReserved(1L, PageRequest.of(0, 10));
        });

        // then
        assertEquals(Errors.NOT_FOUND_STORE_COURSE.getMessage(), exception.getMessage());
    }

}