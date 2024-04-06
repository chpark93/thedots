package com.ch.reservation.controller;

import com.ch.core.common.response.Response;
import com.ch.global.TestConfig;
import com.ch.reservation.service.ReservationHistoryService;
import com.ch.reservation.dto.response.ReservationHistoryResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(ReservationHistoryController.class)
@Import({ReservationHistoryController.class, Response.class})
@ContextConfiguration(classes = TestConfig.class)
class ReservationHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReservationHistoryService reservationHistoryService;
    private ReservationHistoryResponse response;

    @BeforeEach
    void setUp() {
        response = new ReservationHistoryResponse();
    }


    @DisplayName("Get Reservations By Reserved Course Test - Success")
    @Test
    void getReservationsByReserved_success() throws Exception {

        // given
        given(reservationHistoryService.searchReservationsByReserved(any(Long.class), any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/reservations/history/{storeCourseId}/reservedCourse", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Get Reservations By Reserved Course"))
                .andExpect(jsonPath("$.data.storeCourseId").value(response.getStoreCourseId()))
                .andExpect(jsonPath("$.data.storeName").value(response.getStoreName()))
                .andExpect(jsonPath("$.data.courseName").value(response.getCourseName()));
    }

    @DisplayName("Get Reservations By All Course Test - Success")
    @Test
    void getReservationsByAll_success() throws Exception {

        // given
        given(reservationHistoryService.searchReservationsByAll(any(Long.class), any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/reservations/history/{storeCourseId}/allCourse", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Get Reservations By All Course"))
                .andExpect(jsonPath("$.data.storeCourseId").value(response.getStoreCourseId()))
                .andExpect(jsonPath("$.data.storeName").value(response.getStoreName()))
                .andExpect(jsonPath("$.data.courseName").value(response.getCourseName()));
    }
}