package com.leetjourney.device_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leetjourney.device_service.dto.DeviceDto;
import com.leetjourney.device_service.service.DeviceService;

@RestController 
@RequestMapping("/api/v1/device")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable Long id){
        DeviceDto foundDevice = deviceService.getDeviceById(id);
        return ResponseEntity.ok(foundDevice);
    }

    @PostMapping("/create")
    public ResponseEntity<DeviceDto> createDevice(@RequestBody DeviceDto deviceDto){
        DeviceDto createdDeviceDto = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(createdDeviceDto, HttpStatus.CREATED);
    }

    @PutMapping("/updated/{id}")
    public ResponseEntity<DeviceDto> updateDeviceById(@PathVariable Long id, @RequestBody DeviceDto deviceDto){
        DeviceDto updated = deviceService.updateDeviceById(id, deviceDto);
        return ResponseEntity.ok(updated);
    }
    
    @PatchMapping("/patch/{id}")
    public ResponseEntity<DeviceDto> partialDeviceUpdate(@PathVariable Long id, @RequestBody DeviceDto deviceDto){
        DeviceDto updated = deviceService.partialDeviceUpdate(id, deviceDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> DeleteDevice(@PathVariable Long id){
        deviceService.deleteDeviceById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceDto>> getAllDevicesByUserId(@PathVariable Long userId){
        List<DeviceDto> deviceDtos = deviceService.getAllDevicesByUserId(userId);
        return ResponseEntity.ok(deviceDtos);
    }
}
