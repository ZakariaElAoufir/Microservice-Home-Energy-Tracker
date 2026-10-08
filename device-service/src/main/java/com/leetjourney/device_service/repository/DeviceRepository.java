package com.leetjourney.device_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leetjourney.device_service.entity.Device;

import java.util.List;
import java.util.Optional;


@Repository 
public interface DeviceRepository extends JpaRepository<Device, Long> {
    public Optional<Device> findById(Long id);
    public List<Device> findByUserId(Long userId);
}
