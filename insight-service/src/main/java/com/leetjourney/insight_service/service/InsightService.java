package com.leetjourney.insight_service.service;

import org.springframework.stereotype.Service;

import com.leetjourney.insight_service.dto.InsightDto;

import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j
public class InsightService {

    private final UsageClient usageClient;

    public InsightService(UsageClient usageClient) {
        this.usageClient = usageClient;
    }

    public InsightDto getOverview(Long userId){
        // Fetch Data from Usage-Service
        final UsageDto usageData = usageClient.getXDaysUsageForUser(userId, 3);
    }

    
}
