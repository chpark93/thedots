package com.ch.reservation.repository;

import com.ch.core.config.QuerydslConfig;
import com.ch.course.model.Course;
import com.ch.course.model.Store;
import com.ch.course.model.StoreCourse;
import com.ch.global.TestReservationData;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.user.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(QuerydslConfig.class)
class ReservationHistoryRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    ReservationHistoryRepository reservationHistoryRepository;
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    private StoreCourse storeCourse;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        // 사용자 정보
        User user = TestReservationData.createSampleUserData();
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
    }

    @DisplayName("Find Distinct By OrderBy ReservationDate Desc Test - Success")
    @Test
    void findDistinctByOrderByReservationDateDesc_success() {

        // when
        Page<Reservation> reservations = reservationHistoryRepository.findDistinctByOrderByReservationDateDesc(
                storeCourse, ReservationStatus.RESERVE, PageRequest.of(0, 10)
        );

        // then
        assertThat(reservations).hasSize(1);
        assertThat(reservations.getContent().get(0)).isEqualTo(reservation);
    }
}