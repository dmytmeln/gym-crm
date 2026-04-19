package com.gym.crm.service;

public interface ProfileCredentialService {

    String generateUsername(String firstName, String lastName);

    String generatePassword();

}
