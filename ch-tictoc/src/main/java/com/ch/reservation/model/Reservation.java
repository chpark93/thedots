package com.ch.reservation.model;

import com.ch.core.model.code.Errors;
import com.ch.core.model.BaseEntity;
import com.ch.core.exception.BusinessException;
import com.ch.course.model.StoreCourse;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "reservations",
    indexes = {
        @Index(name = "idx_store_course_reservation_date", columnList = "store_course_id, reservation_date"),
        @Index(name = "idx_user_store_course", columnList = "user_id, store_course_id"),
        @Index(name = "idx_user_reservation", columnList = "user_id, id"),
        @Index(name = "idx_reservation_date", columnList = "reservation_date")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="store_course_id", nullable = false)
    private StoreCourse storeCourse;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus;

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass(this) != Hibernate.getClass(o) ) return false;
        Reservation reservation = (Reservation) o;
        return id != null && Objects.equals(id, reservation.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // 예약 취소
    public void cancel() {
        // 당일 및 예약 날짜가 지난 후 취소 불가
        LocalDate today = LocalDate.now();
        if ( this.reservationDate.isBefore(today.plusDays(1)) ) {
           throw new BusinessException(Errors.NOT_POSSIBLE_CANCEL.getMessage(), Errors.NOT_POSSIBLE_CANCEL);
        }

        // ReservationStatus -> Cancel
        reservationStatus = ReservationStatus.CANCEL;
    }

}