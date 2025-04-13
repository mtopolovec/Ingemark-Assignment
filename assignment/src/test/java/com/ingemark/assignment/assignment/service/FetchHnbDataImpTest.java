package com.ingemark.assignment.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ingemark.assignment.assignment.deserializer.HnbStringToBigDecimalDeserializer;
import com.ingemark.assignment.assignment.model.ExchangeRate;
import com.ingemark.assignment.assignment.model.HnbResponse;
import com.ingemark.assignment.assignment.repository.ExchangeRateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FetchHnbDataImpTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private FetchHnbData fetchHnbData;

    private final String url = "https://api.hnb.hr/tecajn-eur/v3?valuta=USD";

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

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new HnbStringToBigDecimalDeserializer());
        objectMapper.registerModule(module);
        fetchHnbData = new FetchHnbDataImp(restTemplate, objectMapper, exchangeRateRepository);
    }

    @AfterEach
    void cleanUp() {
        exchangeRateRepository.deleteAll();
    }

    @Test
    void fetchData_isSuccessful() {
        List<HnbResponse> hnbResponse = List.of(getResponseData());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseDataJson, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class))
                .thenReturn(responseEntity);

        HnbResponse fetchedHnbData = fetchHnbData.fetchData();

        assertThat(fetchedHnbData, is(notNullValue()));
        assertThat(hnbResponse.getFirst(), equalTo(fetchedHnbData));
    }

    @Test
    void fetchData_shouldThrowException_whenResponseIsNotOk() {

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> fetchHnbData.fetchData());
        assertEquals("No fallback exchange rate available.", exception.getMessage());
    }

    @Test
    void fetchData_shouldThrowException_whenJsonCannotBeMapped() {

        String invalidJson = """
                    {
                        "broj_tecajnice": "73",
                        "datum_primjene": "2025-04-14",
                        "drzava": "SAD",
                        "valuta": "USD"
                    }
                """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJson, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> fetchHnbData.fetchData());
        assertTrue(exception.getMessage().contains("Error mapping JSON response to HnbResponse"));
    }

    @Test
    void updateOrSaveHnbResponseToDB_shouldDoNothingIfUpToDate() {
        String currency = "USD";
        LocalDate today = LocalDate.now();
        HnbResponse response = getResponseData();

        ExchangeRate existingRate = new ExchangeRate(currency, response.getBuyingRate(), response.getMiddleRate(), response.getSellingRate(), today);

        when(exchangeRateRepository.findByCurrency(currency)).thenReturn(java.util.Optional.of(existingRate));

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseDataJson, HttpStatus.OK);

        when(restTemplate.exchange(
                "https://api.hnb.hr/tecajn-eur/v3?valuta=USD",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class))
                .thenReturn(responseEntity);

        fetchHnbData.fetchData();

        assertEquals(existingRate.getBuyingRate(), response.getBuyingRate());
        assertEquals(existingRate.getMiddleRate(), response.getMiddleRate());
        assertEquals(existingRate.getSellingRate(), response.getSellingRate());
        assertEquals(existingRate.getDate(), today);

        verify(exchangeRateRepository, never()).save(any());
        verify(exchangeRateRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateOrSaveHnbResponseToDB_shouldSaveIfNotExist() {
        String currency = "USD";
        HnbResponse response = getResponseData();

        when(exchangeRateRepository.findByCurrency(currency)).thenReturn(java.util.Optional.empty());

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseDataJson, HttpStatus.OK);

        when(restTemplate.exchange(
                "https://api.hnb.hr/tecajn-eur/v3?valuta=USD",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class))
                .thenReturn(responseEntity);

        fetchHnbData.fetchData();

        assertEquals(currency, response.getCurrency());
        assertNotNull(response.getBuyingRate());
        assertNotNull(response.getMiddleRate());
        assertNotNull(response.getSellingRate());
        verify(exchangeRateRepository, times(1)).save(any());
        verify(exchangeRateRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateOrSaveHnbResponseToDB_shouldUpdateIfExistsButIsOutdated() {
        String currency = "USD";
        LocalDate today = LocalDate.now();
        ExchangeRate existingRate = new ExchangeRate(currency, new BigDecimal("1.0"), new BigDecimal("1.0"), new BigDecimal("1.0"), today.minusDays(1));

        when(exchangeRateRepository.findByCurrency(currency)).thenReturn(java.util.Optional.of(existingRate));

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseDataJson, HttpStatus.OK);
        when(restTemplate.exchange(
                "https://api.hnb.hr/tecajn-eur/v3?valuta=USD",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class))
                .thenReturn(responseEntity);

        fetchHnbData.fetchData();

        verify(exchangeRateRepository, never()).save(any());
        verify(exchangeRateRepository, times(1)).saveAndFlush(existingRate);
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