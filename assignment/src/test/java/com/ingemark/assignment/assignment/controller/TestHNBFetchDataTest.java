package com.ingemark.assignment.assignment.controller;

import com.ingemark.assignment.assignment.model.HnbResponse;
import com.ingemark.assignment.assignment.service.FetchHnbData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestHNBFetchData.class)
@Import(TestHNBFetchDataTest.MockedServiceConfig.class)
class TestHNBFetchDataTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FetchHnbData fetchHnbData;

    @Test
    void testGetData_returnsHnbResponse() throws Exception {
        HnbResponse mockResponse = new HnbResponse();
        mockResponse.setRateNumber("73");
        mockResponse.setApplicationDate("2025-04-14");
        mockResponse.setCountry("SAD");
        mockResponse.setCountryIso("USA");
        mockResponse.setBuyingRate(new BigDecimal("1.136300"));
        mockResponse.setSellingRate(new BigDecimal("1.132900"));
        mockResponse.setCurrencyCode("840");
        mockResponse.setMiddleRate(new BigDecimal("1.134600"));
        mockResponse.setCurrency("USD");

        when(fetchHnbData.fetchData()).thenReturn(mockResponse);

        mockMvc.perform(get("http://localhost:8080/api/data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valuta").value("USD"))
                .andExpect(jsonPath("$.srednji_tecaj").value("1.1346"));
    }

    @TestConfiguration
    static class MockedServiceConfig {
        @Bean
        public FetchHnbData fetchHnbData() {
            return mock(FetchHnbData.class);
        }
    }
}