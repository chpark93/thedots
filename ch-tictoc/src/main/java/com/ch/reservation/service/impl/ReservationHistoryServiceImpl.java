package com.ch.reservation.service.impl;

import com.ch.core.model.code.Errors;
import com.ch.core.exception.BusinessException;
import com.ch.course.model.StoreCourse;
import com.ch.course.repository.StoreCourseRepository;
import com.ch.reservation.service.ReservationHistoryService;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.model.code.ReservationStatus;
import com.ch.reservation.dto.response.ReservationHistoryResponse;
import com.ch.reservation.repository.ReservationHistoryRepository;
import com.ch.user.model.User;
import com.ch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationHistoryServiceImpl implements ReservationHistoryService {
    private final UserRepository userRepository;
    private final StoreCourseRepository storeCourseRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    /**
     * 매장별, 수업별 예약자 현황
     * @param storeCourseId Store-Course 고유 ID
     * @param pageable Data 페이지네이션
     * @return ReservationHistoryResponse
     */
    @Override
    @Transactional( readOnly = true )
    public ReservationHistoryResponse searchReservationsByReserved(Long storeCourseId, Pageable pageable) {

        // storeCourseId -> StoreCourse Data 조회
        StoreCourse storeCourse = storeCourseRepository.findById(storeCourseId).orElseThrow(
                () -> new BusinessException(Errors.NOT_FOUND_STORE_COURSE.getMessage(), Errors.NOT_FOUND_STORE_COURSE)
        );

        // 날짜별 데이터 수집 - 예약관련 Reserved 정보
        Page<Reservation> reservationsFromPage = reservationHistoryRepository
                .findDistinctByOrderByReservationDateDesc(storeCourse, ReservationStatus.RESERVE, pageable);

        // 예약 날짜별로 그룹핑한 Reservation History 정보
        return makeReservationHistoryResponse(pageable, storeCourse, reservationsFromPage);
    }

    /**
     * 매장별, 수업별 예약 이력 현황
     * @param storeCourseId Store-Course 고유 ID
     * @param pageable Data 페이지네이션
     * @return ReservationHistoryResponse
     */
    @Override
    public ReservationHistoryResponse searchReservationsByAll(Long storeCourseId, Pageable pageable) {

        // storeCourseId -> StoreCourse Data 조회
        StoreCourse storeCourse = storeCourseRepository.findById(storeCourseId).orElseThrow(
                () -> new BusinessException(Errors.NOT_FOUND_STORE_COURSE.getMessage(), Errors.NOT_FOUND_STORE_COURSE)
        );

        // 날짜별 데이터 수집 - 예약관련 전체 정보
        Page<Reservation> reservationsFromPage = reservationHistoryRepository
                .findDistinctByOrderByReservationDateDesc(storeCourse, null, pageable);

        // 예약 날짜별로 그룹핑한 Reservation History 정보
        return makeReservationHistoryResponse(pageable, storeCourse, reservationsFromPage);
    }


    /**
     * 예약 리스트 Response 생성
     * @param pageable 페이지네이션
     * @param storeCourse  Store-Course 객체
     * @param reservationsFromPage 날짜별 데이터 수집 - 예약관련 정보
     * @return ReservationHistoryResponse
     */
    private ReservationHistoryResponse makeReservationHistoryResponse(Pageable pageable, StoreCourse storeCourse, Page<Reservation> reservationsFromPage) {
        List<ReservationHistoryResponse.History> reservationHistoryGroupedByDate = getReservationHistories(reservationsFromPage);

        return ReservationHistoryResponse.builder()
                .storeCourseId(storeCourse.getId())
                .storeName(storeCourse.getStore().getName())
                .courseName(storeCourse.getCourse().getName())
                .content(reservationHistoryGroupedByDate.isEmpty() ? Collections.emptyList() : reservationHistoryGroupedByDate)
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(reservationsFromPage.getTotalElements())
                .totalPages(reservationsFromPage.getTotalPages())
                .last(reservationsFromPage.isLast())
                .build();
    }

    /**
     * Reservation History 정보
     * @param reservationsFromPage 날짜별 데이터 수집
     * @return List<ReservationHistoryResponse.History>
     */
    private List<ReservationHistoryResponse.History> getReservationHistories(Page<Reservation> reservationsFromPage) {
        // 매장-수업 Reservation 정보
        List<Reservation> reservations = reservationsFromPage.get().toList();
        // 해당 User IDs 수집 -> Set 저장
        Set<Long> userIds = getUserIdsFromReservations(reservations);
        // 필요한 Users 객체 가져오기
        Map<Long, User> users = getUserByUserIds(userIds);
        // 날짜별 Info 데이터 세팅
        return getReservationHistoryGroupedByDate(reservations, users);
    }

    /**
     * 매장-수업 Reservation -> 예약자 고유 ID 값 가져오기
     * @param reservations 매장-수업 Reservation 정보
     * @return Set<Long>
     */
    private Set<Long> getUserIdsFromReservations(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> reservation.getUser().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 예약자 Ids -> 예약자 정보
     * @param userIds 해당 User 고유 IDs
     * @return Map<Long, User>
     */
    private Map<Long, User> getUserByUserIds(Set<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    /**
     * 예약 날짜별로 그룹핑한 Reservation History 정보
     * @param reservations 매장-수업 Reservation 정보
     * @param users getUserByUserIds -> 조회된 예약자 정보
     * @return List<ReservationHistoryResponse.History>
     */
    private List<ReservationHistoryResponse.History> getReservationHistoryGroupedByDate(List<Reservation> reservations, Map<Long, User> users) {
        return reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getReservationDate))
                .entrySet().stream()
                .map(entry -> makeReservationHistory(entry.getKey(), entry.getValue(), users))
                .sorted(Comparator.comparing(ReservationHistoryResponse.History::getDate).reversed())
                .toList();
    }

    /**
     * 날짜별 예약 History 생성
     * @param date 예약 날짜
     * @param reservationHistoryGroupedByDate 예약 날짜별로 그룹핑한 Reservation History 정보
     * @param users 예약자 Ids -> 예약자 정보
     * @return ReservationHistoryResponse.History
     */
    private ReservationHistoryResponse.History makeReservationHistory(LocalDate date, List<Reservation> reservationHistoryGroupedByDate, Map<Long, User> users) {
        // 개별 예약 정보 List 조회
        List<ReservationHistoryResponse.Info> infos = reservationHistoryGroupedByDate.stream()
                .map(reservation -> makeReservationHistoryInfoFromReservation(reservation, users))
                .toList();

        // 예약 날짜-수업별 Total Count 계산
        int totalCount = calculateTotalCount(infos);

        return ReservationHistoryResponse.History.builder()
                .date(date)
                .totalCount(totalCount)
                .infos(infos)
                .build();
    }

    /**
     * 개별 예약 정보 생성
     * @param reservation 예약 정보
     * @param users 예약자 Ids -> 예약자 정보
     * @return ReservationHistoryResponse.Info
     */
    private ReservationHistoryResponse.Info makeReservationHistoryInfoFromReservation(Reservation reservation, Map<Long, User> users) {
        User user = users.get(reservation.getUser().getId());
        return ReservationHistoryResponse.Info.builder()
                .userId(user.getId())
                .reservationId(reservation.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .count(reservation.getCount())
                .status(reservation.getReservationStatus())
                .build();
    }

    /**
     * 수업-예약 날짜별 Total Count 계산
     * @param reservationHistoryInfos 개별 예약 정보
     * @return int
     */
    private int calculateTotalCount(List<ReservationHistoryResponse.Info> reservationHistoryInfos) {
        return reservationHistoryInfos.stream()
                .mapToInt(ReservationHistoryResponse.Info::getCount)
                .sum();
    }

}
