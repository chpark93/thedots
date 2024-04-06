package com.ch.reservation.repository;

import com.ch.core.config.QuerydslConfig;
import com.ch.course.model.Course;
import com.ch.course.model.Store;
import com.ch.course.model.StoreCourse;
import com.ch.global.TestReservationData;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(QuerydslConfig.class)
class ReservationRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    ReservationRepository reservationRepository;

    private User user;
    private StoreCourse storeCourse;
    private Reservation reservation;
    private ReservationRequest.Create createRequest;
    private ReservationRequest.Cancel cancelRequest;

    @BeforeEach
    void setUp() {
        // 사용자 정보
        user = TestReservationData.createSampleUserData();
        testEntityManager.merge(user);
        // Course 정보
        Course course = TestReservationData.createSampleCourseData();
        testEntityManager.merge(course);
        // Store 정보
        Store store = TestReservationData.createSampleStoreData();
        testEntityManager.merge(store);
        // 수업 정보
        storeCourse = TestReservationData.createSampleStoreCourseData(store, course);
        testEntityManager.merge(storeCourse);
        // 예약 정보
        reservation = TestReservationData.createSampleReservationData(user, storeCourse);
        testEntityManager.merge(reservation);
        // 생성 요청
        createRequest = TestReservationData.createSampleCreateData(storeCourse);
        // 취소 요청
        cancelRequest = TestReservationData.createSampleCancelData(reservation);

    }

    @DisplayName("Exists By UserId And StoreCourseId And ReservationStatus Test - Success")
    @Test
    void existsByUserIdAndStoreCourseIdAndReservationStatus_success() {

        // when
        boolean exists = reservationRepository
                .existsByUserIdAndStoreCourseIdAndReservationStatus(user.getId(), createRequest.getStoreCourseId(), ReservationStatus.RESERVE);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("Find By Id And UserId And ReservationStatus Test - Success")
    @Test
    void findByUserIdAndIdAndReservationStatus_success() {

        // when
        Optional<Reservation> foundReservation = reservationRepository
                .findByUserIdAndIdAndReservationStatus(user.getId(), cancelRequest.getReservationId(), ReservationStatus.RESERVE);

        // then
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation).contains(reservation);
    }

    @DisplayName("FindAll By StoreCourseId And ReservationDate And ReservationStatus Test - Success")
    @Test
    void findAllByStoreCourseIdAndReservationDateAndReservationStatus_success() {

        // when
        Optional<List<Reservation>> foundReservations = reservationRepository
                .findAllByStoreCourseIdAndReservationDateAndReservationStatus(createRequest.getStoreCourseId(), createRequest.getReservationDate(), ReservationStatus.RESERVE);

        // then
        assertThat(foundReservations).isPresent();
        assertThat(foundReservations.get()).contains(reservation);
    }
}