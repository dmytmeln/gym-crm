package com.gym.crm.controller;

import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.facade.GymFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthRestController {

    private final GymFacade facade;

    @PutMapping("/auth/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody LoginChangeRequest loginChangeRequest) {
        facade.changePassword(loginChangeRequest.getUsername(), loginChangeRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        facade.login(loginRequest);

        return ResponseEntity.ok().build();
    }

}

