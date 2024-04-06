package com.ch.reservation.dto.request;

import com.ch.core.annotation.WithinDays;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "사용자 수업 예약 Request")
public class ReservationRequest {

    private ReservationRequest() {}

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        @NotNull(message = "storeCourseId Field Cannot Be Null")
        @Schema(description = "StoreCourse 고유 ID", example = "1")
        private Long storeCourseId;

        @NotNull(message = "count Field Cannot Be Null")
        @Positive(message = "Invalid count Field")
        @Schema(description = "예약 인원수", example = "10")
        private Integer count;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @NotNull(message = "reservationDate Field Cannot Be Null")
        @WithinDays(value = 14)
        @Schema(description = "예약 날짜", example = "2024-04-10")
        private LocalDate reservationDate;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cancel {
        @NotNull(message = "reservationId Field Cannot Be Null")
        @Schema(description = "Reservation 고유 ID", example = "1")
        private Long reservationId;
    }

}
