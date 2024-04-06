package com.ch.global;

import com.ch.course.model.Address;
import com.ch.course.model.Course;
import com.ch.course.model.Store;
import com.ch.course.model.StoreCourse;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.user.model.User;

import java.time.LocalDate;
import java.time.YearMonth;

public class TestReservationData {

    public static User createSampleUserData() {
        String email = "test@test.com";
        String name = "동탁";

        return User.builder()
                .id(1L)
                .email(email)
                .name(name)
                .build();
    }

    public static Store createSampleStoreData() {

        Address address = Address.builder()
                .city("테스트")
                .street("테스트")
                .zipCode("100")
                .build();

        return Store.builder()
                .id(1L)
                .name("테스트")
                .address(address)
                .build();
    }

    public static Course createSampleCourseData() {

        return Course.builder()
                .id(1L)
                .count(20)
                .name("테스트")
                .build();
    }

    public static StoreCourse createSampleStoreCourseData(Store store, Course course) {

        return StoreCourse.builder()
                .id(1L)
                .store(store)
                .course(course)
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build();
    }

    public static Reservation createSampleReservationData(User user, StoreCourse storeCourse) {
        LocalDate today = LocalDate.now();
        LocalDate daysLater = today.plusDays(2);

        return Reservation.builder()
                .id(1L)
                .user(user)
                .storeCourse(storeCourse)
                .count(3)
                .reservationDate(daysLater)
                .reservationStatus(ReservationStatus.RESERVE)
                .build();
    }

    public static Reservation createSampleReservationCancelData(User user, StoreCourse storeCourse) {
        LocalDate today = LocalDate.now();
        LocalDate daysLater = today.plusDays(2);

        return Reservation.builder()
                .id(1L)
                .user(user)
                .storeCourse(storeCourse)
                .count(3)
                .reservationDate(daysLater)
                .reservationStatus(ReservationStatus.CANCEL)
                .build();
    }

    public static ReservationRequest.Create createSampleCreateData(StoreCourse storeCourse) {

        return new ReservationRequest.Create(
                storeCourse.getId(), 10, LocalDate.now().plusDays(2)
        );
    }

    public static ReservationRequest.Cancel createSampleCancelData(Reservation reservation) {

        return new ReservationRequest.Cancel(
                reservation.getId()
        );
    }
}
