package com.gym.crm.service;

import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.LoginRequestDto;

public interface AuthenticationService {

    String login(LoginRequestDto loginRequestDto);

    void changePassword(LoginChangeDto loginChangeDto);

}
