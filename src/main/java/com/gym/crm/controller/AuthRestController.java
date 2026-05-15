package com.gym.crm.controller;

import com.gia.openapi.api.AuthApi;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.facade.GymFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthRestController implements AuthApi {

    private final GymFacade facade;

    @Override
    public ResponseEntity<Void> changePassword(@Valid LoginChangeRequest loginChangeRequest) {
        facade.changePassword(loginChangeRequest.getUsername(), loginChangeRequest);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> login(@Valid LoginRequest loginRequest) {
        facade.login(loginRequest);

        return ResponseEntity.ok().build();
    }

}

