package com.gym.crm.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@ToString(exclude = "password")
@SuperBuilder(toBuilder = true)
public abstract class User {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
