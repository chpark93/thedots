package com.ch.reservation.controller;

import com.ch.core.common.response.Response;
import com.ch.reservation.dto.response.ReservationHistoryResponse;
import com.ch.reservation.service.ReservationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation History API",
        description = "째깍섬 예약 현황 API")
@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationHistoryController {
    private final Response response;
    private final ReservationHistoryService reservationHistoryService;

    @Operation(summary = "Get Reserved Reservations API", description = "매장별, 수업별 예약자 현황 API", responses = {
            @ApiResponse(responseCode = "200", description = "OK <br><br> API 요청에 대해 정상적으로 진행 될 경우, data 필드에 해당 데이터들이 담겨 응답됩니다.", content = @Content(schema = @Schema(implementation = ReservationHistoryResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BAD_REQUEST"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/FORBIDDEN"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NOT_FOUND"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/INTERNAL_SERVER_ERROR")
    })
    @GetMapping("/history/{storeCourseId}/reservedCourse")
    public ResponseEntity<Response.Body> searchReservationsByReserved(@Parameter(description = "매장-수업 고유 ID", example = "1", required = true) @PathVariable Long storeCourseId
                                                                      ,@RequestParam(value = "page", defaultValue = "0") int page
                                                                      ,@RequestParam(value = "size", defaultValue = "5") int size) {

        return response.success(reservationHistoryService.searchReservationsByReserved(storeCourseId
                , PageRequest.of(page, size, Sort.by("reservationDate").descending()))
                , "Get Reservations By Reserved Course", HttpStatus.OK);
    }

    @Operation(summary = "Get All Reservations API", description = "매장별, 수업별 예약 이력 현황 API", responses = {
            @ApiResponse(responseCode = "200", description = "OK <br><br> API 요청에 대해 정상적으로 진행 될 경우, data 필드에 해당 데이터들이 담겨 응답됩니다.", content = @Content(schema = @Schema(implementation = ReservationHistoryResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BAD_REQUEST"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/FORBIDDEN"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NOT_FOUND"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/INTERNAL_SERVER_ERROR")
    })
    @GetMapping("/history/{storeCourseId}/allCourse")
    public ResponseEntity<Response.Body> searchReservationsByAll(@Parameter(description = "매장-수업 고유 ID", example = "1", required = true) @PathVariable Long storeCourseId
                                                                 ,@RequestParam(value = "page", defaultValue = "0") int page
                                                                 ,@RequestParam(value = "size", defaultValue = "5") int size) {

        return response.success(reservationHistoryService.searchReservationsByAll(storeCourseId
                , PageRequest.of(page, size, Sort.by("reservationDate").descending()))
                , "Get Reservations By All Course", HttpStatus.OK);
    }

}
