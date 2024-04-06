package com.ch.reservation.dto.response;

import com.ch.reservation.model.code.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "수업 예약 현황 Response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationHistoryResponse {

    // History
    @Schema(description = "매장-수업 고유 ID", example = "1")
    private Long storeCourseId;
    @Schema(description = "매장 이름", example = "잠실점")
    private String storeName;
    @Schema(description = "수업 이름", example = "드로잉")
    private String courseName;
    @Schema(description = "예약 리스트")
    private List<ReservationHistoryResponse.History> content;

    // Page
    @Schema(description = "현재 페이지 번호", example = "0")
    private int pageNo;
    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;
    @Schema(description = "전체 데이터 수", example = "100")
    private long totalElements;
    @Schema(description = "전체 페이지 수", example = "10")
    private int totalPages;
    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class History {
        @Schema(description = "예약 날짜", example = "2024-04-10")
        private LocalDate date;
        @Schema(description = "총 예약 인원", example = "10")
        private Integer totalCount = 0;
        @Schema(description = "예약 세부 리스트")
        private List<Info> infos = new ArrayList<>();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Info {
        @Schema(description = "사용자 고유 ID", example = "1")
        private Long userId;
        @Schema(description = "예약 고유 ID", example = "1")
        private Long reservationId;
        @Schema(description = "사용자 이름", example = "조조")
        private String userName;
        @Schema(description = "사용자 이메일", example = "test@test.com")
        private String email;
        @Schema(description = "예약 상태", example = "RESERVED")
        private ReservationStatus status;
        @Schema(description = "예약 인원 수", example = "2")
        private Integer count;
    }
}
