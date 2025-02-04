package com.example.filmservice.controller;

import com.example.filmservice.model.AppUser;
import com.example.filmservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // Registrera användare
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody AppUser user, BindingResult bindingResult) {
        if (userService.getUserByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Username already exists\"}");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid input data\"}");
        }

        AppUser savedUser = userService.createUser(user);

        return ResponseEntity.ok().body("{\"message\": \"User registered successfully\", \"redirect\": \"/home\"}");
    }
}