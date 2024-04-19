package com.ch.reservation.controller;

import com.ch.core.common.response.Response;
import com.ch.core.utils.HelperUtil;
import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.reservation.dto.response.ReservationResponse;
import com.ch.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation API", description = "예약 API")
@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final Response response;
    private final ReservationService reservationService;

    @Operation(summary = "Reservation Create API", description = "수업 예약 API", responses = {
            @ApiResponse(responseCode = "200", description = "OK <br><br> API 요청에 대해 정상적으로 진행 될 경우, data 필드에 해당 데이터들이 담겨 응답됩니다."
                    , content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BAD_REQUEST"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/FORBIDDEN"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NOT_FOUND"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/INTERNAL_SERVER_ERROR")
    })
    @Parameter(name = "storeCourseId", description = "StoreCourse 고유 ID", example = "1", required = true)
    @Parameter(name = "count", description = "예약 인원수", example = "10", required = true)
    @Parameter(name = "reservationDate", description = "예약 날짜", example = "2024-04-10", required = true)
    @PostMapping("/{userId}/booking")
    public ResponseEntity<Response.Body> createReservation(@Parameter(description = "사용자 고유 ID", example = "1", required = true) @PathVariable Long userId,
                                                           @RequestBody @Validated ReservationRequest.Create reservationRequest, Errors errors) {
        // validation check
        if ( errors.hasErrors() ) {
            return response.invalidFields(HelperUtil.refineErrors(errors));
        }

        return response.success(reservationService.createReservation(userId, reservationRequest), "Saving Data Is Success", HttpStatus.OK);
    }

    @Operation(summary = "Reservation Cancel API", description = "수업 예약 취소 API", responses = {
            @ApiResponse(responseCode = "200", description = "OK <br><br> API 요청에 대해 정상적으로 진행 될 경우, data 필드에 해당 데이터들이 담겨 응답됩니다.", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BAD_REQUEST"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/FORBIDDEN"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NOT_FOUND"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/INTERNAL_SERVER_ERROR")
    })
    @Parameter(name = "reservationId", description = "Reservation 고유 ID", example = "1", required = true)
    @DeleteMapping("/{userId}/booking")
    public ResponseEntity<Response.Body> cancelReservation(@Parameter(description = "사용자 고유 ID", example = "1", required = true) @PathVariable Long userId
            , @RequestBody @Validated ReservationRequest.Cancel reservationRequest, Errors errors) {
        // validation check
        if ( errors.hasErrors() ) {
            return response.invalidFields(HelperUtil.refineErrors(errors));
        }

        return response.success(reservationService.cancelReservation(userId, reservationRequest), "Canceling Data Is Success", HttpStatus.OK);
    }

}
