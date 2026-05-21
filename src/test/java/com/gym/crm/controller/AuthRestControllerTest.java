package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.entity.EntityType;
import com.gym.crm.exception.ApiError;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.GlobalRestExceptionHandler;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.security.AuthenticationException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    private static final String BASE_PATH = "/api/v1";
    private static final String USERNAME = "liam.miller";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword123";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GymFacade facade;

    @InjectMocks
    private AuthRestController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalRestExceptionHandler())
                .setValidator(validatorFactoryBean)
                .addPlaceholderValue("app.api.base-path", BASE_PATH)
                .build();
    }

    @Test
    void shouldLoginWhenCredentialsAreValid() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);

        mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).login(validRequest);
    }

    @Test
    void shouldFailLoginWhenUsernameIsNull() throws Exception {
        LoginRequest invalidRequestWithNullUsername = buildLoginRequest(null, PASSWORD);

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullUsername)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: username: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailLoginWhenPasswordIsNull() throws Exception {
        LoginRequest invalidRequestWithNullPassword = buildLoginRequest(USERNAME, null);

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullPassword)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: password: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn404WhenLoginUserNotFound() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);
        EntityNotFoundException exception = EntityNotFoundException.forUsername(EntityType.USER, USERNAME);

        doThrow(exception)
                .when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.NOT_FOUND.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("%s: %s".formatted(ApiError.NOT_FOUND.getMessage(), exception.getMessage()));
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);

        doThrow(new RuntimeException("Unexpected failure"))
                .when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.SERVICE.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(ApiError.SERVICE.getMessage());
    }

    @Test
    void shouldReturn500WhenHibernateExceptionOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);

        doThrow(new HibernateException("Database connectivity failure"))
                .when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.DATABASE.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(ApiError.DATABASE.getMessage());
    }

    @Test
    void shouldChangePasswordWhenRequestIsValid() throws Exception {
        LoginChangeRequest validRequest = buildLoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);

        mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).changePassword(eq(USERNAME), any(LoginChangeRequest.class));
    }

    @Test
    void shouldFailChangePasswordWhenUsernameIsNull() throws Exception {
        LoginChangeRequest invalidRequestWithNullUsername = buildLoginChangeRequest(null, PASSWORD, NEW_PASSWORD);

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullUsername)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: username: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenOldPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequestWithNullOldPassword = buildLoginChangeRequest(USERNAME, null, NEW_PASSWORD);

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullOldPassword)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: oldPassword: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenNewPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequestWithNullNewPassword = buildLoginChangeRequest(USERNAME, PASSWORD, null);

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullNewPassword)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: newPassword: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn401WhenAuthenticationFailsDuringPasswordChange() throws Exception {
        LoginChangeRequest validRequest = buildLoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);

        doThrow(new AuthenticationException("User is not authenticated"))
                .when(facade).changePassword(eq(USERNAME), any(LoginChangeRequest.class));

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.AUTHENTICATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(ApiError.AUTHENTICATION.getMessage());
    }

    @Test
    void shouldReturn400WhenValidationExceptionOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);
        ValidationException exception = new ValidationException("Custom validation failed");

        doThrow(exception)
                .when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("%s: %s".formatted(ApiError.VALIDATION.getMessage(), exception.getMessage()));
    }

    private LoginRequest buildLoginRequest(String username, String password) {
        return new LoginRequest()
                .username(username)
                .password(password);
    }

    private LoginChangeRequest buildLoginChangeRequest(String username, String oldPassword, String newPassword) {
        return new LoginChangeRequest()
                .username(username)
                .oldPassword(oldPassword)
                .newPassword(newPassword);
    }

}
