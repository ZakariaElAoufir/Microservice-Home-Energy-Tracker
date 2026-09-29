package com.leetjourney.user_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.leetjourney.user_service.entity.User;
import com.leetjourney.user_service.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j 
class UserServiceApplicationTests {
	private static final int NUMBER_OF_USERS = 10;
	@Autowired 
	private UserRepository userRepository;

	@Test
	void contextLoads() {
	}

	@Disabled 
	@Test
	void createUsers(){
		for (int i = 0; i < NUMBER_OF_USERS; i++) {
			var users = User.builder()
						.name("User_"+i)
						.surname("Surname_"+i)
						.email("user"+i+"@exemple.com")
						.address(i+" Exemple St")
						.alerting(i % 2 == 0)
						.energyAlertingThreshold(10000.0 + i)
						.build();
			userRepository.save(users);
		}
		log.info("User Repository populated successfully");
	}

}
