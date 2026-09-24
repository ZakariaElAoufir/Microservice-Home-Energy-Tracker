package com.leetjourney.user_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.leetjourney.user_service.dto.UserDto;
import com.leetjourney.user_service.entity.User;
import com.leetjourney.user_service.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto input){
        final User createdUser = toUser(input);
        final User saved = userRepository.save(createdUser);
        return toUserDto(saved);
    }

    public UserDto getUserById(Long id){
        return userRepository.findById(id)
                            .map(user -> toUserDto(user))
                            .orElse(null);
    }

    public void updateUserById(Long id, UserDto userDto){
        User user = userRepository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setName(userDto.getName());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAlerting(userDto.isAlerting());
        user.setEnergyAlertingThreshold(userDto.getEnergyAlertingThreshold());

        userRepository.save(user);
    }

    public void deleteUserById(Long id){
        User user = userRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    private User toUser(UserDto input) {
        return User.builder()
                    .name(input.getName())
                    .surname(input.getSurname())
                    .email(input.getEmail())
                    .address(input.getAddress())
                    .alerting(input.isAlerting())
                    .energyAlertingThreshold(input.getEnergyAlertingThreshold())
                    .build();
    }

    private UserDto toUserDto(User input) {
        return UserDto.builder()
                    .id(input.getId())
                    .name(input.getName())
                    .surname(input.getSurname())
                    .email(input.getEmail())
                    .address(input.getAddress())
                    .alerting(input.isAlerting())
                    .energyAlertingThreshold(input.getEnergyAlertingThreshold())
                    .build();
    }

}
