package com.ingemark.assignment.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.assignment.assignment.model.ExchangeRate;
import com.ingemark.assignment.assignment.model.HnbResponse;
import com.ingemark.assignment.assignment.repository.ExchangeRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static com.ingemark.assignment.assignment.model.Currency.USD;

@Slf4j
@Service
public class FetchHnbDataImp implements FetchHnbData {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ExchangeRateRepository exchangeRateRepository;

    public FetchHnbDataImp(RestTemplate restTemplate, ObjectMapper objectMapper, ExchangeRateRepository exchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public HnbResponse fetchData() {
        String url = "https://api.hnb.hr/tecajn-eur/v3?valuta=" + USD.name();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            HnbResponse hnbResponse = mappedData(response.getBody());
            updateOrSaveHnbResponseToDB(hnbResponse);
            return hnbResponse;
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            log.debug("Request to fetch data failed with status code: {}", response.getStatusCode());
            return exchangeRateRepository
                    .findByCurrency(USD.name())
                    .map(er -> new HnbResponse(
                            USD.name(),
                            er.getBuyingRate(),
                            er.getMiddleRate(),
                            er.getSellingRate())
                    )
                    .orElseThrow(() -> new RuntimeException("No fallback exchange rate available."));
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

    private void updateOrSaveHnbResponseToDB(HnbResponse response) {
        if (response == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        String currency = response.getCurrency() != null ? response.getCurrency() : USD.name();

        exchangeRateRepository.findByCurrency(currency).ifPresentOrElse(
                existing -> {
                    if (!today.equals(existing.getDate())) {
                        existing.setBuyingRate(response.getBuyingRate());
                        existing.setMiddleRate(response.getMiddleRate());
                        existing.setSellingRate(response.getSellingRate());
                        existing.setDate(today);
                        exchangeRateRepository.saveAndFlush(existing);
                        log.info("Updated exchange rate for currency {}.", currency);
                    } else {
                        log.info("Exchange rate for currency {} is already up to date.", currency);
                    }
                },
                () -> {
                    ExchangeRate newRate = new ExchangeRate(
                            currency,
                            response.getBuyingRate(),
                            response.getMiddleRate(),
                            response.getSellingRate(),
                            today
                    );
                    exchangeRateRepository.save(newRate);
                    log.info("Saved new exchange rate for currency {}.", currency);
                }
        );
    }
}
