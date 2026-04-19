package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.User;
import com.gym.crm.service.ProfileCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class ProfileCredentialServiceImpl implements ProfileCredentialService {

    private static final String PASSWORD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final int PASSWORD_LENGTH = 10;
    private static final String USERNAME_SEPARATOR = ".";

    private final SecureRandom secureRandom = new SecureRandom();

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public String generateUsername(String firstName, String lastName) {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);

        String baseUsername = firstName + USERNAME_SEPARATOR + lastName;

        List<String> takenUsernames = findUsernamesWithSameBase(baseUsername);
        if (takenUsernames.isEmpty()) {
            return baseUsername;
        }

        int serialNumber = nextSerialNumber(baseUsername, takenUsernames);
        return baseUsername + serialNumber;
    }

    private List<String> findUsernamesWithSameBase(String baseUsername) {
        return findAllUsernames()
                .filter(username -> matchesBaseUsername(username, baseUsername))
                .collect(toList());
    }

    private Stream<String> findAllUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream(),
                        trainerDao.findAll().stream())
                .map(User::getUsername);
    }

    private boolean matchesBaseUsername(String username, String baseUsername) {
        String lowerUsername = username.toLowerCase();
        String lowerBaseUsername = baseUsername.toLowerCase();

        if (!lowerUsername.startsWith(lowerBaseUsername)) {
            return false;
        }

        if (lowerUsername.equals(lowerBaseUsername)) {
            return true;
        }

        String suffix = lowerUsername.substring(lowerBaseUsername.length());
        return suffix.matches("\\d+");
    }

    private int nextSerialNumber(String baseUsername, List<String> existingUsernames) {
        int maxSerialNumber = findMaxSerialNumber(baseUsername, existingUsernames);
        return maxSerialNumber + 1;
    }

    private int findMaxSerialNumber(String baseUsername, List<String> existingUsernames) {
        return existingUsernames.stream()
                .mapToInt(username -> extractSerialNumber(username, baseUsername))
                .max()
                .orElse(0);
    }

    private int extractSerialNumber(String username, String baseUsername) {
        String suffix = username.substring(baseUsername.length());
        return suffix.isEmpty()
                ? 0
                : Integer.parseInt(suffix);
    }

    @Override
    public String generatePassword() {
        char[] password = new char[PASSWORD_LENGTH];
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int alphabetIndex = secureRandom.nextInt(PASSWORD_ALPHABET.length());
            password[i] = PASSWORD_ALPHABET.charAt(alphabetIndex);
        }

        return new String(password);
    }

}
