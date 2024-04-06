package com.ch.reservation.dto.response;

import com.ch.reservation.model.code.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 수업 예약 Response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    @Schema(description = "사용자 고유 ID", example = "1")
    private Long userId;
    @Schema(description = "예약 고유 ID", example = "1")
    private Long reservationId;
    @Schema(description = "예약 Status", example = "RESERVE, CANCEL")
    private ReservationStatus reservationStatus;
}
