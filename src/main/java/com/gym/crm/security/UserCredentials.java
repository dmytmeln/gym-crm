package com.gym.crm.security;

import lombok.Builder;

@Builder
public record UserCredentials(
        String username,
        Role role
) {
}
