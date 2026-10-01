package com.leetjourney.alert_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leetjourney.alert_service.entity.Alert;

@Repository 
public interface AlertRepository extends JpaRepository<Alert, Long> {
}
