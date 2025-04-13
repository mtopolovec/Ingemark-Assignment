package com.ingemark.assignment.assignment.scheduler;

import com.ingemark.assignment.assignment.service.FetchHnbData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HnbScheduler {
    private final FetchHnbData fetchHnbData;

    @PostConstruct
    public void fetchOnStartup() {
        log.info("Fetching HNB data on app startup...");
        fetchHnbData.fetchData();
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void fetchPeriodically() {
        log.info("Scheduled fetch of HNB data...");
        fetchHnbData.fetchData();
    }
}
