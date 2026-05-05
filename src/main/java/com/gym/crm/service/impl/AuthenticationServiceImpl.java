package com.gym.crm.service.impl;

import com.gym.crm.dto.LoginRequestDto;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.security.SecurityContext;
import com.gym.crm.security.UserCredentials;
import com.gym.crm.service.AuthenticationService;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TrainerService trainerService;
    private final TraineeService traineeService;

    @Override
    public void login(LoginRequestDto loginRequestDto) {
        Objects.requireNonNull(loginRequestDto, "AuthenticateRequestDto cannot be null");
        log.info("Login attempt for username: {} with role: {}", loginRequestDto.username(), loginRequestDto.role());

        if (!doesUsernameAndPasswordMatch(loginRequestDto)) {
            throw new AuthenticationException("Invalid username or password");
        }

        UserCredentials authenticatedUser = new UserCredentials(loginRequestDto.username(), loginRequestDto.role());
        SecurityContext.setCurrentUser(authenticatedUser);
        log.info("Login successful for username: {}", loginRequestDto.username());
    }

    @Override
    public void logout() {
        SecurityContext.clear();
    }

    private boolean doesUsernameAndPasswordMatch(LoginRequestDto loginRequestDto) {
        return switch (loginRequestDto.role()) {
            case TRAINER ->
                    trainerService.doesUsernameAndPasswordMatch(loginRequestDto.username(), loginRequestDto.password());
            case TRAINEE ->
                    traineeService.doesUsernameAndPasswordMatch(loginRequestDto.username(), loginRequestDto.password());
        };
    }

}
