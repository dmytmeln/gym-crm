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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private static final String USERNAME = "liam.miller";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword123";

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeService traineeService;

    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationServiceImpl(trainerService, traineeService);
    }

    @AfterEach
    void tearDown() {
        SecurityContext.clear();
    }

    @Test
    void shouldLoginWhenCredentialsMatchTrainer() {
        LoginRequestDto loginRequestDto = buildLoginRequestDto();

        when(trainerService.doesUsernameAndPasswordMatch(USERNAME, PASSWORD)).thenReturn(true);

        service.login(loginRequestDto);

        UserCredentials currentUser = SecurityContext.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals(USERNAME, currentUser.username());
        assertEquals(Role.TRAINER, currentUser.role());
    }

    @Test
    void shouldLoginWhenCredentialsMatchTrainee() {
        LoginRequestDto loginRequestDto = buildLoginRequestDto();

        when(trainerService.doesUsernameAndPasswordMatch(USERNAME, PASSWORD)).thenReturn(false);
        when(traineeService.doesUsernameAndPasswordMatch(USERNAME, PASSWORD)).thenReturn(true);

        service.login(loginRequestDto);

        UserCredentials currentUser = SecurityContext.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals(USERNAME, currentUser.username());
        assertEquals(Role.TRAINEE, currentUser.role());
    }

    @Test
    void shouldThrowExceptionWhenLoginCredentialsAreInvalid() {
        LoginRequestDto loginRequestDto = buildLoginRequestDto();

        when(trainerService.doesUsernameAndPasswordMatch(USERNAME, PASSWORD)).thenReturn(false);
        when(traineeService.doesUsernameAndPasswordMatch(USERNAME, PASSWORD)).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> service.login(loginRequestDto));
    }

    @Test
    void shouldChangePasswordForTrainee() {
        LoginChangeDto loginChangeDto = buildLoginChangeDto();
        SecurityContext.setCurrentUser(new UserCredentials(USERNAME, Role.TRAINEE));

        service.changePassword(loginChangeDto);

        verify(traineeService).updateTraineePassword(loginChangeDto);
    }

    @Test
    void shouldChangePasswordForTrainer() {
        LoginChangeDto loginChangeDto = buildLoginChangeDto();
        SecurityContext.setCurrentUser(new UserCredentials(USERNAME, Role.TRAINER));

        service.changePassword(loginChangeDto);

        verify(trainerService).updateTrainerPassword(loginChangeDto);
    }

    private LoginRequestDto buildLoginRequestDto() {
        return new LoginRequestDto(USERNAME, PASSWORD);
    }

    private LoginChangeDto buildLoginChangeDto() {
        return new LoginChangeDto(USERNAME, PASSWORD, NEW_PASSWORD);
    }

}
