package com.ingemark.assignment.assignment.controller;

import com.ingemark.assignment.assignment.model.HnbResponse;
import com.ingemark.assignment.assignment.service.FetchHnbData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/data")
public class TestHNBFetchData {
    private final FetchHnbData fetchHnbData;

    public TestHNBFetchData(FetchHnbData fetchHnbData) {
        this.fetchHnbData = fetchHnbData;
    }

    @GetMapping
    public ResponseEntity<HnbResponse> getData() {
        log.debug("Get data from HNB");
        return new ResponseEntity<>(fetchHnbData.fetchData(), HttpStatus.OK);
    }
}
