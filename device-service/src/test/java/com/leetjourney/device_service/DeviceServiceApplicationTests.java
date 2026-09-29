package com.leetjourney.device_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.leetjourney.device_service.entity.Device;
import com.leetjourney.device_service.model.DeviceType;
import com.leetjourney.device_service.repository.DeviceRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j 
class DeviceServiceApplicationTests {

	private static final int USERS = 10;
	private static final int NUMBER_OF_DEVICES = 200;
	@Autowired 
	private DeviceRepository deviceRepository;

	@Test
	void contextLoads() {
	}

	@Disabled 
	@Test
	void createDevices(){
		for (int i = 0; i < NUMBER_OF_DEVICES; i++) {
			var device = Device.builder()
								.name("Device_"+i)
								.type(DeviceType.values()[i%DeviceType.values().length])
								.location("Location_"+((i%3)+1))
								.userId((long)((i % USERS)+1))
								.build();
			deviceRepository.save(device);
		}
		log.info("Device Repository has been populated");
	}

}
