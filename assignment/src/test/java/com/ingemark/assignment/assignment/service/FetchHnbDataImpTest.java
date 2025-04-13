package com.ingemark.assignment.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ingemark.assignment.assignment.deserializer.HnbStringToBigDecimalDeserializer;
import com.ingemark.assignment.assignment.model.HnbResponse;
import com.ingemark.assignment.assignment.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class FetchHnbDataImpTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private FetchHnbData fetchHnbData;

    private final String url = "https://api.hnb.hr/tecajn-eur/v3?valuta=USD";

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new HnbStringToBigDecimalDeserializer());
        objectMapper.registerModule(module);
        fetchHnbData = new FetchHnbDataImp(restTemplate, objectMapper, exchangeRateRepository);
    }

    @Test
    void fetchData_isSuccessful() {
        HttpEntity<String> entity = new HttpEntity<>(setUpHeaders());
        String responseDataJson =
                """
                    [
                      {
                        "broj_tecajnice": "73",
                        "datum_primjene": "2025-04-14",
                        "drzava": "SAD",
                        "drzava_iso": "USA",
                        "kupovni_tecaj": "1,136300",
                        "prodajni_tecaj": "1,132900",
                        "sifra_valute": "840",
                        "srednji_tecaj": "1,134600",
                        "valuta": "USD"
                      }
                    ]
                """;
        List<HnbResponse> hnbResponse = List.of(getResponseData());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseDataJson, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, String.class))
                .thenReturn(responseEntity);

        HnbResponse fetchedHnbData = fetchHnbData.fetchData();

        assertThat(fetchedHnbData, is(notNullValue()));
        assertThat(hnbResponse.getFirst(), equalTo(fetchedHnbData));
    }

    @Test
    void fetchData_shouldThrowException_whenResponseIsNotOk() {
        HttpEntity<String> entity = new HttpEntity<>(setUpHeaders());

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, String.class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> fetchHnbData.fetchData());
        assertEquals("Request to fetch data failed with status code: 404 NOT_FOUND", exception.getMessage());
    }

    @Test
    void fetchData_shouldThrowException_whenJsonCannotBeMapped() {
        HttpEntity<String> entity = new HttpEntity<>(setUpHeaders());

        String invalidJson = """
                    {
                        "broj_tecajnice": "73",
                        "datum_primjene": "2025-04-14",
                        "drzava": "SAD",
                        "valuta": "USD"
                    }
                """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJson, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, String.class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> fetchHnbData.fetchData());
        assertTrue(exception.getMessage().contains("Error mapping JSON response to HnbResponse"));
    }

    private HttpHeaders setUpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HnbResponse getResponseData() {
        HnbResponse hnbResponse = new HnbResponse();
        hnbResponse.setRateNumber("73");
        hnbResponse.setApplicationDate("2025-04-14");
        hnbResponse.setCountry("SAD");
        hnbResponse.setCountryIso("USA");
        hnbResponse.setBuyingRate(new BigDecimal("1.136300"));
        hnbResponse.setSellingRate(new BigDecimal("1.132900"));
        hnbResponse.setCurrencyCode("840");
        hnbResponse.setMiddleRate(new BigDecimal("1.134600"));
        hnbResponse.setCurrency("USD");

        return hnbResponse;
    }
}