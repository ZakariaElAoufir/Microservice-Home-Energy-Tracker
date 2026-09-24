package com.leetjourney.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leetjourney.user_service.dto.UserDto;
import com.leetjourney.user_service.service.UserService;

@RestController 
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto created = userService.createUser(userDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        UserDto userDto = userService.getUserById(id);
        if (userDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return ResponseEntity.ok(userDto);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserById(@PathVariable Long id, 
                                                @RequestBody UserDto userDto){
        try {
            userService.updateUserById(id, userDto);
            return ResponseEntity.ok("User Updated successfully");
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
