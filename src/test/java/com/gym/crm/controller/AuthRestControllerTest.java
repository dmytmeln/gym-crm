package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.facade.GymFacade;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullUsername)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailLoginWhenPasswordIsNull() throws Exception {
        LoginRequest invalidRequestWithNullPassword = buildLoginRequest(USERNAME, null);

        mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullPassword)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
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

        mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullUsername)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenOldPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequestWithNullOldPassword = buildLoginChangeRequest(USERNAME, null, NEW_PASSWORD);

        mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullOldPassword)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenNewPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequestWithNullNewPassword = buildLoginChangeRequest(USERNAME, PASSWORD, null);

        mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullNewPassword)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
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
