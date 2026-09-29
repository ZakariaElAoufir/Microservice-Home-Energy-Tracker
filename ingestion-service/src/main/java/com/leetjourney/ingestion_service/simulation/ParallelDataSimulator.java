package com.leetjourney.ingestion_service.simulation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.leetjourney.ingestion_service.dto.EnergyUsageDto;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j 
public class ParallelDataSimulator implements CommandLineRunner {
    
    private final ExecutorService executorService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();
    @Value("${simulation.parallel-threads}")
    private int parallelThreads;

    @Value("${simulation.requests-per-interval}")
    private int requestPerInterval;

    @Value("${simulation.endpoint}")
    private String ingestionEndpoint;
    

    public ParallelDataSimulator() {
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("ParallelDataSimulator Started...");
        ((ThreadPoolExecutor)executorService).setCorePoolSize(parallelThreads);
    }

    @Scheduled(fixedRateString = "${simulation.interval-ms}")
    public void sendMockData(){
        int batchSize = requestPerInterval / parallelThreads;
        int remainder = requestPerInterval % parallelThreads;
        for (int i = 0; i < parallelThreads; i++) {
            int requestsForThread = batchSize + (i < remainder ? 1 : 0);
            executorService.submit(()-> {
                for (int j = 0; j < requestsForThread; j++) {
                    EnergyUsageDto dto = EnergyUsageDto.builder()
                                                .deviceId(random.nextLong(1,6))
                                                .energyConsumed(Math.round(random.nextDouble(0.0,2.0) * 100) / 100)
                                                .timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                                                .build();
                    try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<EnergyUsageDto> request = new HttpEntity<>(dto, headers);
                    restTemplate.postForEntity(ingestionEndpoint, request, Void.class);
                    log.info("Sent Mock data: {}",dto);

                    } catch (Exception e) {
                        log.error("failed to send data: {}", e.getMessage());
                    }                    

                }
            });
        }

    }

    @PreDestroy
    public void shutdown(){
        executorService.shutdown();
        log.info("ParallelDataSimulator shutdown...");

    }

    
}
