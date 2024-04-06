package com.ch.reservation.repository.impl;

import com.ch.course.model.StoreCourse;
import com.ch.reservation.model.QReservation;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.reservation.repository.custom.ReservationHistoryRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.ch.reservation.model.QReservation.reservation;

@Repository
public class ReservationHistoryRepositoryImpl extends QuerydslRepositorySupport implements ReservationHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReservationHistoryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Reservation.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Reservation> findDistinctByOrderByReservationDateDesc(StoreCourse storeCourse, ReservationStatus reservationStatus, Pageable pageable) {

        QReservation reservation = QReservation.reservation;
        List<Reservation> query = jpaQueryFactory.selectFrom(reservation)
                .where(
                        reservation.storeCourse.eq(storeCourse),
                        this.reservationDateBetween(14),
                        this.reservationStatusEq(reservationStatus),
                        reservation.reservationDate.in(
                                JPAExpressions
                                        .select(reservation.reservationDate)
                                        .distinct()
                                        .from(reservation)
                                        .where(
                                                reservation.storeCourse.eq(storeCourse),
                                                this.reservationDateBetween(14),
                                                this.reservationStatusEq(reservationStatus)
                                        )
                                        .orderBy(reservation.reservationDate.desc())
                        )
                )
                .orderBy(reservation.reservationDate.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        long totalCount = query.size();
        return PageableExecutionUtils.getPage(query, pageable, () -> totalCount);
    }

    private BooleanExpression reservationStatusEq(ReservationStatus reservationStatus) {
        return reservationStatus != null ? reservation.reservationStatus.eq(reservationStatus) : null;
    }

    private BooleanExpression reservationDateBetween(int plusDays) {
        LocalDate today = LocalDate.now();
        LocalDate daysLater = today.plusDays(plusDays);

        return reservation.reservationDate.between(today.plusDays(1), daysLater);
    }
}
