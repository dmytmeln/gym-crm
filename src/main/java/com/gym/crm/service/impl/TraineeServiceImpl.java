package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ConflictException;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import com.gym.crm.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gym.crm.entity.EntityType.TRAINEE;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private static final String USERNAME_NULL_MSG = "Trainee username cannot be null";

    private TraineeDao traineeDao;
    private TrainingDao trainingDao;
    private ProfileCredentialGenerator credentialGenerator;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setCredentialGenerator(ProfileCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee cannot be null");
        Objects.requireNonNull(trainee.getUser(), "User cannot be null");
        log.info("Creating trainee: {} {}", trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        String username = credentialGenerator.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName());
        String password = credentialGenerator.generatePassword();

        User userWithCredentials = trainee.getUser().toBuilder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        Trainee traineeWithCredentials = trainee.toBuilder()
                .user(userWithCredentials)
                .build();

        Trainee createdTrainee = traineeDao.save(traineeWithCredentials);
        log.info("Trainee created with ID: {} and username: {}", createdTrainee.getId(), createdTrainee.getUser().getUsername());

        User userWithRawPassword = createdTrainee.getUser().toBuilder()
                .password(password)
                .build();
        return createdTrainee.toBuilder()
                .user(userWithRawPassword)
                .build();
    }

    @Override
    public Trainee getTraineeByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));
    }

    @Override
    public List<Trainer> getAvailableTrainers(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Getting available trainers for trainee username: {}", username);

        traineeDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));

        return traineeDao.findTraineeAvailableTrainers(username);
    }

    @Override
    public List<Training> getTrainingsByCriteria(TraineeTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        log.info("Getting trainings by criteria for trainee: {}", filter.getUsername());

        return trainingDao.findTraineeTrainingsByCriteria(filter);
    }

    @Override
    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        Objects.requireNonNull(password, "Password cannot be null");
        log.info("Checking if username and password match for trainee username: {}", username);

        Optional<Trainee> traineeOptional = traineeDao.findByUsername(username);
        if (traineeOptional.isEmpty()) {
            log.warn("Trainee not found with username: {}", username);
            return false;
        }

        Trainee trainee = traineeOptional.get();
        boolean passwordMatches = passwordEncoder.matches(password, trainee.getUser().getPassword());
        if (!passwordMatches) {
            log.warn("Passwords do not match for trainee username: {}", username);
            return false;
        }

        log.info("Passwords match for trainee username: {}", username);
        return true;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee cannot be null");
        Objects.requireNonNull(trainee.getUser(), "User cannot be null");
        String username = trainee.getUser().getUsername();
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Updating trainee with username: {}", username);

        Trainee existingTrainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));

        User updatedUser = existingTrainee.getUser().toBuilder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .isActive(trainee.getUser().getIsActive())
                .build();
        Trainee mergedTrainee = existingTrainee.toBuilder()
                .user(updatedUser)
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .build();

        Trainee updatedTrainee = traineeDao.update(mergedTrainee);
        log.info("Trainee with username: {} updated successfully", username);

        return updatedTrainee;
    }

    @Override
    @Transaction
    public Trainee updateTraineeTrainers(String username, List<String> trainerUsernames) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        Objects.requireNonNull(trainerUsernames, "Trainer usernames cannot be null");
        log.info("Updating trainers for trainee with username: {}", username);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));
        List<Trainer> trainers = traineeDao.findTraineeTrainersByUsernames(trainerUsernames);

        if (trainers.size() != trainerUsernames.size()) {
            log.warn("Some trainers were not found for usernames: {}", trainerUsernames);
        }

        trainee.getTrainers().clear();
        trainers.forEach(trainee::addTrainer);

        Trainee updatedTrainee = traineeDao.update(trainee);
        log.info("Trainers successfully updated for trainee with username: {}", username);

        return updatedTrainee;
    }

    @Override
    public void updateTraineePassword(LoginChangeDto loginChangeDto) {
        Objects.requireNonNull(loginChangeDto, "LoginChangeDto cannot be null");
        log.info("Updating password for trainee with username: {}", loginChangeDto.username());

        if (!doesUsernameAndPasswordMatch(loginChangeDto.username(), loginChangeDto.oldPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Trainee trainee = getTraineeByUsername(loginChangeDto.username());
        User updatedUser = trainee.getUser().toBuilder()
                .password(passwordEncoder.encode(loginChangeDto.newPassword()))
                .build();
        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);
        log.info("Password for trainee with username: {} updated successfully", loginChangeDto.username());
    }

    @Override
    public void updateActivationStatus(String username, boolean isActive) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Updating activation status for trainee with username: {} to {}", username, isActive);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));

        if (Objects.equals(trainee.getUser().getIsActive(), isActive)) {
            throw new ConflictException(String.format("Trainee with username: %s is already %s", username, isActive ? "active" : "inactive"));
        }

        User updatedUser = trainee.getUser().toBuilder()
                .isActive(isActive)
                .build();
        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);
        log.info("Activation status for trainee with username: {} updated successfully", username);
    }

    @Override
    public boolean deleteTraineeByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Deleting trainee with username: {}", username);

        boolean deleted = traineeDao.deleteByUsername(username);
        if (!deleted) {
            log.warn("Trainee with username: {} not found for deletion", username);
            return false;
        }

        log.info("Trainee with username: {} deleted successfully", username);
        return true;
    }

}
