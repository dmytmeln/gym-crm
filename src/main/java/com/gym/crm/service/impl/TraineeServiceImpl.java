package com.gym.crm.service.impl;

import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.User;
import com.gym.crm.exception.ConflictException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.specification.TraineeTrainingCriteriaBuilder;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gym.crm.entity.EntityType.TRAINEE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final String USERNAME_NULL_MSG = "Trainee username cannot be null";

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileCredentialGenerator credentialGenerator;
    private final PasswordEncoder passwordEncoder;
    private final TraineeTrainingCriteriaBuilder trainingCriteriaBuilder;

    @Override
    @Transactional
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

        Trainee createdTrainee = traineeRepository.save(traineeWithCredentials);
        log.info("Trainee created with ID: {} and username: {}", createdTrainee.getId(), createdTrainee.getUser().getUsername());

        User userWithRawPassword = createdTrainee.getUser().toBuilder()
                .password(password)
                .build();
        return createdTrainee.toBuilder()
                .user(userWithRawPassword)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        return traineeRepository.findByUsernameWithUserAndTrainersDetails(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAvailableTrainers(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Getting available trainers for trainee username: {}", username);

        if (!traineeRepository.existsByUserUsername(username)) {
            throw EntityNotFoundException.forUsername(TRAINEE, username);
        }

        return trainerRepository.findTraineeAvailableTrainers(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(TraineeTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        log.info("Getting trainings by criteria for trainee: {}", filter.getUsername());

        return trainingRepository.findAll(trainingCriteriaBuilder.build(filter));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        Objects.requireNonNull(password, "Password cannot be null");
        log.info("Checking if username and password match for trainee username: {}", username);

        Optional<Trainee> traineeOptional = traineeRepository.findByUsernameWithUser(username);
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
    @Transactional
    public Trainee updateTrainee(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee cannot be null");
        Objects.requireNonNull(trainee.getUser(), "User cannot be null");
        String username = trainee.getUser().getUsername();
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Updating trainee with username: {}", username);

        Trainee existingTrainee = traineeRepository.findByUsernameWithUserAndTrainersDetails(username)
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

        Trainee updatedTrainee = traineeRepository.save(mergedTrainee);
        log.info("Trainee with username: {} updated successfully", username);

        return updatedTrainee;
    }

    @Override
    @Transactional
    public Trainee updateTraineeTrainers(String username, List<String> trainerUsernames) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        Objects.requireNonNull(trainerUsernames, "Trainer usernames cannot be null");
        log.info("Updating trainers for trainee with username: {}", username);

        Trainee trainee = traineeRepository.findByUsernameWithUserAndTrainersDetails(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, username));
        List<Trainer> trainers = trainerRepository.findTraineeTrainersByUsernames(trainerUsernames);

        if (trainers.size() != trainerUsernames.size()) {
            log.warn("Some trainers were not found for usernames: {}", trainerUsernames);
        }

        trainee.getTrainers().clear();
        trainers.forEach(trainee::addTrainer);

        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainers successfully updated for trainee with username: {}", username);

        return updatedTrainee;
    }

    @Override
    @Transactional
    public void updateTraineePassword(LoginChangeDto loginChangeDto) {
        Objects.requireNonNull(loginChangeDto, "LoginChangeDto cannot be null");
        log.info("Updating password for trainee with username: {}", loginChangeDto.username());

        Trainee trainee = traineeRepository.findByUsernameWithUser(loginChangeDto.username())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(loginChangeDto.oldPassword(), trainee.getUser().getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        User updatedUser = trainee.getUser().toBuilder()
                .password(passwordEncoder.encode(loginChangeDto.newPassword()))
                .build();
        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeRepository.save(updatedTrainee);
        log.info("Password for trainee with username: {} updated successfully", loginChangeDto.username());
    }

    @Override
    @Transactional
    public void updateActivationStatus(String username, boolean isActive) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Updating activation status for trainee with username: {} to {}", username, isActive);

        Trainee trainee = traineeRepository.findByUsernameWithUser(username)
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

        traineeRepository.save(updatedTrainee);
        log.info("Activation status for trainee with username: {} updated successfully", username);
    }

    @Override
    @Transactional
    public boolean deleteTraineeByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Deleting trainee with username: {}", username);

        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
        if (traineeOpt.isEmpty()) {
            log.warn("Trainee with username: {} not found for deletion", username);
            return false;
        }

        traineeRepository.delete(traineeOpt.get());
        log.info("Trainee with username: {} deleted successfully", username);
        return true;
    }

}
