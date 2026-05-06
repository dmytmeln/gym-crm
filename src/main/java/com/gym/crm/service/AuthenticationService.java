package com.gym.crm.service;

import com.gym.crm.dto.LoginRequestDto;

public interface AuthenticationService {

    void login(LoginRequestDto loginRequestDto);

    void logout();

}
