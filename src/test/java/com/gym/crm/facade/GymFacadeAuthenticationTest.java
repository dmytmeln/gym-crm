package com.gym.crm.facade;

import com.gia.openapi.model.LoginRequest;
import com.gym.crm.GymCrmApplication;
import com.gym.crm.config.BaseDbIntegrationTest;
import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.security.Role;
import com.gym.crm.security.SecurityContext;
import com.gym.crm.security.UserCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(GymCrmApplication.class)
@Sql(scripts = {"classpath:datasets/cleanup-all.sql", "classpath:datasets/seed-data.sql"})
class GymFacadeAuthenticationTest extends BaseDbIntegrationTest {

    private static final String TRAINEE_USERNAME = "liam.miller";
    private static final String TRAINEE_PASSWORD = "password123";

    @Autowired
    private GymFacade gymFacade;

    @AfterEach
    void tearDown() {
        SecurityContext.clear();
    }

    @Test
    void shouldSetUserInSecurityContextAfterLogin() {
        LoginRequest loginDto = new LoginRequest().username(TRAINEE_USERNAME).password(TRAINEE_PASSWORD);
        UserCredentials expected = new UserCredentials(TRAINEE_USERNAME, Role.TRAINEE);

        gymFacade.login(loginDto);

        assertThat(SecurityContext.getCurrentUser()).isEqualTo(expected);
    }

    @Test
    void shouldAllowCreateTraineeWithoutLogin() {
        TraineeCreateDto dto = TraineeCreateDto.builder()
                .firstName("New")
                .lastName("Trainee")
                .active(true)
                .address("Address")
                .dateOfBirth(LocalDate.now())
                .build();

        TraineeCreateResponseDto response = gymFacade.createTrainee(dto);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo(dto.firstName());
        assertThat(response.lastName()).isEqualTo(dto.lastName());
    }

    @Test
    void shouldAllowCreateTrainerWithoutLogin() {
        TrainerCreateDto dto = TrainerCreateDto.builder()
                .firstName("New")
                .lastName("Trainer")
                .active(true)
                .specializationId(1L)
                .build();

        TrainerCreateResponseDto response = gymFacade.createTrainer(dto);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo(dto.firstName());
        assertThat(response.lastName()).isEqualTo(dto.lastName());
    }

    @Test
    void shouldDenyGetTraineeWithoutLogin() {
        assertThatThrownBy(() -> gymFacade.getTrainee(TRAINEE_USERNAME, 1L))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("User is not authenticated");
    }

    @Test
    void shouldAllowGetTraineeAfterLogin() {
        LoginRequest loginDto = new LoginRequest().username(TRAINEE_USERNAME).password(TRAINEE_PASSWORD);
        gymFacade.login(loginDto);

        TraineeResponseDto response = gymFacade.getTrainee(TRAINEE_USERNAME, 1L);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo("Liam");
        assertThat(response.lastName()).isEqualTo("Miller");
        assertThat(response.username()).isEqualTo("liam.miller");
    }

    @Test
    void shouldThrowExceptionOnInvalidLogin() {
        LoginRequest loginDto = new LoginRequest().username("invalid").password("wrongPassword123");
        assertThatThrownBy(() -> gymFacade.login(loginDto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }

}