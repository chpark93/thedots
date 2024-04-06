package com.ch.reservation.controller;

import com.ch.core.common.response.Response;
import com.ch.course.model.StoreCourse;
import com.ch.global.TestConfig;
import com.ch.global.TestReservationData;
import com.ch.reservation.service.ReservationService;
import com.ch.reservation.model.Reservation;
import com.ch.reservation.dto.request.ReservationRequest;
import com.ch.reservation.dto.response.ReservationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(ReservationController.class)
@Import({ReservationController.class, Response.class})
@ContextConfiguration(classes = TestConfig.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReservationService reservationService;

    private ReservationRequest.Create createRequest;
    private ReservationRequest.Cancel cancelRequest;

    @BeforeEach
    void setUp() {
        // 수업 정보
        StoreCourse storeCourse = TestReservationData.createSampleStoreCourseData(TestReservationData.createSampleStoreData(), TestReservationData.createSampleCourseData());
        // 예약 정보
        Reservation reservation = TestReservationData.createSampleReservationData(TestReservationData.createSampleUserData(), storeCourse);
        // 생성 요청
        createRequest = TestReservationData.createSampleCreateData(storeCourse);
        // 취소 요청
        cancelRequest = TestReservationData.createSampleCancelData(reservation);
    }

    @DisplayName("Create Reservations Test - Success")
    @Test
    void createReservation_success() throws Exception {

        // given
        given(reservationService.createReservation(any(Long.class), any(ReservationRequest.Create.class))).willReturn(new ReservationResponse());

        // when, then
        mockMvc.perform(post("/reservations/{userId}/booking", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("Cancel Reservations Test - Success")
    @Test
    void cancelReservation_success() throws Exception {

        // given
        given(reservationService.cancelReservation(any(Long.class), any(ReservationRequest.Cancel.class))).willReturn(new ReservationResponse());

        // when, then
        mockMvc.perform(delete("/reservations/{userId}/booking", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk());
    }
}