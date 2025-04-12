package com.ingemark.assignment.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.assignment.assignment.model.HnbResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.ingemark.assignment.assignment.model.Currency.USD;

@Slf4j
@Service
public class FetchHnbDataImp implements FetchHnbData {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FetchHnbDataImp(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public HnbResponse fetchData() {
        String url = "https://api.hnb.hr/tecajn-eur/v3?valuta=" + USD.name();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            log.debug("Request to fetch data failed with status code: {}", response.getStatusCode());
            throw new RuntimeException("Request to fetch data failed with status code: " + response.getStatusCode());
        }

        return mappedData(response.getBody());
    }

    private HnbResponse mappedData(String data) {
        try {
            List<HnbResponse> exchangeRates = objectMapper.readValue(
                    data,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, HnbResponse.class)
            );
            return exchangeRates.isEmpty() ? null : exchangeRates.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping JSON response to HnbResponse", e);
        }
    }
}
