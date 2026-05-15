package com.gym.crm.service.impl;

import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.LoginRequestDto;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.security.Role;
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
        log.info("Login attempt for username: {}", loginRequestDto.username());

        Role role = deduceRole(loginRequestDto.username(), loginRequestDto.password());
        if (role == null) {
            throw new AuthenticationException("Invalid username or password");
        }

        UserCredentials authenticatedUser = new UserCredentials(loginRequestDto.username(), role);
        SecurityContext.setCurrentUser(authenticatedUser);
        log.info("Login successful for username: {} with role: {}", loginRequestDto.username(), role);
    }

    @Override
    public void changePassword(LoginChangeDto loginChangeDto) {
        Objects.requireNonNull(loginChangeDto, "LoginChangeDto cannot be null");
        log.info("Change password attempt for username: {}", loginChangeDto.username());

        Role role = SecurityContext.getCurrentUser().role();
        switch (role) {
            case TRAINEE -> traineeService.updateTraineePassword(loginChangeDto);
            case TRAINER -> trainerService.updateTrainerPassword(loginChangeDto);
        }
        
        log.info("Password changed successfully for username: {}", loginChangeDto.username());
    }

    private Role deduceRole(String username, String password) {
        if (trainerService.doesUsernameAndPasswordMatch(username, password)) {
            return Role.TRAINER;
        }

        if (traineeService.doesUsernameAndPasswordMatch(username, password)) {
            return Role.TRAINEE;
        }

        return null;
    }

}
