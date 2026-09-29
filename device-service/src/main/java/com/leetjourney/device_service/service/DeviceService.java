package com.leetjourney.device_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.leetjourney.device_service.dto.DeviceDto;
import com.leetjourney.device_service.entity.Device;
import com.leetjourney.device_service.exception.DeviceNotFoundException;
import com.leetjourney.device_service.repository.DeviceRepository;

@Service 
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDto getDeviceById(Long id){
        Device device = deviceRepository.findById(id).orElseThrow(()-> new DeviceNotFoundException("Device not Found!"));
        return mapToDto(device);
    }

    public DeviceDto createDevice(DeviceDto deviceDto){
        Device device = mapToEntity(deviceDto);
        Device created = deviceRepository.save(device);
        return mapToDto(created);
    }

    public DeviceDto updateDeviceById(Long id, DeviceDto deviceDto){
        Device found = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException("Device not found!"));
        found.setName(deviceDto.getName());
        found.setLocation(deviceDto.getLocation());
        found.setType(deviceDto.getType());
        found.setUserId(deviceDto.getUserId());

        Device updated = deviceRepository.save(found);
        return mapToDto(updated);

    }

    public void deleteDeviceById(Long id){
        Device found = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException("Device not found!"));
        deviceRepository.delete(found);
    }

    public DeviceDto partialDeviceUpdate(Long id, DeviceDto deviceDto) {
    return deviceRepository.findById(id)
        .map(existing -> {
            // 1. Mise à jour conditionnelle (ta logique est parfaite ici)
            Optional.ofNullable(deviceDto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(deviceDto.getLocation()).ifPresent(existing::setLocation);
            Optional.ofNullable(deviceDto.getType()).ifPresent(existing::setType);
            Optional.ofNullable(deviceDto.getUserId()).ifPresent(existing::setUserId);

            // 2. Sauvegarde en base de données
            Device saved = deviceRepository.save(existing);

            // 3. Retourne le DTO (c'est ce que .map() attend)
            return mapToDto(saved);
        })
        .orElseThrow(() -> new DeviceNotFoundException("Device not found"));
    }

    private DeviceDto mapToDto(Device device) {
        return DeviceDto.builder()
                .id(device.getId())
                .name(device.getName())
                .type(device.getType())
                .location(device.getLocation())
                .userId(device.getUserId())
                .build();
    }

    private Device mapToEntity(DeviceDto device) {
        return Device.builder()
                .name(device.getName())
                .type(device.getType())
                .location(device.getLocation())
                .userId(device.getUserId())
                .build();
    }
    
}
