package com.gym.crm.generator;

public interface ProfileCredentialGenerator {

    String generateUsername(String firstName, String lastName);

    String generatePassword();

}
