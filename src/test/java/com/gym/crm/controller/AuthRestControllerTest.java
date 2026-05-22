package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.exception.ApiError;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.GlobalExceptionHandler;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static com.gym.crm.entity.EntityType.USER;
import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.exception.ApiError.VALIDATION_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    private static final String EXPECTED_ERROR_MESSAGE_TEMPLATE = "%s: %s";
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
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validatorFactoryBean)
                .addPlaceholderValue("app.api.base-path", BASE_PATH)
                .build();
    }

    @Test
    void shouldLoginWhenCredentialsAreValid() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);

        mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).login(validRequest);
    }

    @Test
    void shouldFailLoginWhenUsernameIsNull() throws Exception {
        LoginRequest invalidRequest = buildLoginRequest(null, PASSWORD);

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: username: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailLoginWhenPasswordIsNull() throws Exception {
        LoginRequest invalidRequest = buildLoginRequest(USERNAME, null);

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: password: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn404WhenLoginUserNotFound() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);
        EntityNotFoundException exception = EntityNotFoundException.forUsername(USER, USERNAME);

        doThrow(exception).when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(NOT_FOUND_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(buildExpectedErrorMessage(NOT_FOUND_ERROR, exception));
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);

        doThrow(new RuntimeException("Unexpected failure")).when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(SERVICE_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(SERVICE_ERROR.getMessage());
    }

    @Test
    void shouldReturn500WhenHibernateExceptionOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);

        doThrow(new HibernateException("Database connectivity failure")).when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(DATABASE_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(DATABASE_ERROR.getMessage());
    }

    @Test
    void shouldChangePasswordWhenRequestIsValid() throws Exception {
        LoginChangeRequest validRequest = buildLoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);

        mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).changePassword(eq(USERNAME), any(LoginChangeRequest.class));
    }

    @Test
    void shouldFailChangePasswordWhenUsernameIsNull() throws Exception {
        LoginChangeRequest invalidRequest = buildLoginChangeRequest(null, PASSWORD, NEW_PASSWORD);

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: username: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenOldPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequest = buildLoginChangeRequest(USERNAME, null, NEW_PASSWORD);

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: oldPassword: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenNewPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequest = buildLoginChangeRequest(USERNAME, PASSWORD, null);

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: newPassword: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn401WhenAuthenticationFailsDuringPasswordChange() throws Exception {
        LoginChangeRequest validRequest = buildLoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);

        doThrow(new AuthenticationException("User is not authenticated")).when(facade).changePassword(eq(USERNAME), any(LoginChangeRequest.class));

        String content = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(AUTHENTICATION_ERROR.getMessage());
    }

    @Test
    void shouldReturn400WhenValidationExceptionOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(USERNAME, PASSWORD);
        ValidationException exception = new ValidationException("Custom validation failed");

        doThrow(exception).when(facade).login(any());

        String content = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(buildExpectedErrorMessage(VALIDATION_ERROR, exception));
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

    private String buildExpectedErrorMessage(ApiError apiError, Exception exception) {
        return String.format(EXPECTED_ERROR_MESSAGE_TEMPLATE, apiError.getMessage(), exception.getMessage());
    }

}
