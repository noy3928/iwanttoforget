package com.soak.soak.controller;

import com.soak.soak.payload.request.LoginRequest;
import com.soak.soak.payload.request.SignupRequest;
import com.soak.soak.payload.response.JwtResponse;
import com.soak.soak.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.soak.soak.service.AuthService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Error: Authentication failed"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse messageResponse = authService.registerUser(signUpRequest);
        if (messageResponse.getMessage().startsWith("Error")) {
            return ResponseEntity.badRequest().body(messageResponse);
        }
        return ResponseEntity.ok(messageResponse);
    }
}
