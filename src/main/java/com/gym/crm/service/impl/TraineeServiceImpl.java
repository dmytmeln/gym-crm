package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeEntityDao;
import com.gym.crm.dao.TrainerEntityDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.helper.ProfileCredentialGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeEntityDao traineeDao;
    private TrainerEntityDao trainerDao;
    private ProfileCredentialGenerator credentialGenerator;

    @Autowired
    public void setTraineeDao(TraineeEntityDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerEntityDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setCredentialGenerator(ProfileCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
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
                .password(password)
                .build();

        Trainee traineeWithCredentials = trainee.toBuilder()
                .user(userWithCredentials)
                .build();

        Trainee createdTrainee = traineeDao.save(traineeWithCredentials);
        log.info("Trainee created with ID: {} and username: {}", createdTrainee.getId(), createdTrainee.getUser().getUsername());

        return createdTrainee;
    }

    @Override
    public Trainee getTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        return traineeDao.findById(traineeId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", traineeId));
    }

    @Override
    public Trainee getTraineeByUsername(String username) {
        Objects.requireNonNull(username, "Trainee username cannot be null");

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername("Trainee", username));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

    @Override
    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(password, "Password cannot be null");
        log.info("Checking if username and password match for trainee username: {}", username);

        Trainee trainee = getTraineeByUsername(username);

        boolean passwordsMatch = Objects.equals(trainee.getUser().getPassword(), password);
        if (!passwordsMatch) {
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
        Objects.requireNonNull(trainee.getId(), "Trainee ID cannot be null");
        log.info("Updating trainee with ID: {}", trainee.getId());

        Trainee existingTrainee = traineeDao.findById(trainee.getId())
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", trainee.getId()));

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
        log.info("Trainee with ID: {} and username: {} updated successfully", updatedTrainee.getId(), updatedTrainee.getUser().getUsername());

        return updatedTrainee;
    }

    @Override
    public Trainee updateTraineeTrainers(Long traineeId, List<Long> trainerIds) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        Objects.requireNonNull(trainerIds, "Trainer IDs cannot be null");
        log.info("Updating trainers for trainee with ID: {}", traineeId);

        Trainee trainee = traineeDao.findById(traineeId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", traineeId));

        List<Trainer> trainers = trainerDao.findAllByIds(trainerIds);

        trainee.getTrainers().clear();
        trainers.forEach(trainee::addTrainer);

        Trainee updatedTrainee = traineeDao.update(trainee);
        log.info("Trainers successfully updated for trainee with ID: {}", updatedTrainee.getId());

        return updatedTrainee;
    }

    @Override
    public void updateTraineePassword(Long traineeId, String newPassword) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        Objects.requireNonNull(newPassword, "Password cannot be null");
        log.info("Updating password for trainee with ID: {}", traineeId);

        Trainee trainee = traineeDao.findById(traineeId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", traineeId));

        User updatedUser = trainee.getUser().toBuilder()
                .password(newPassword)
                .build();

        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);

        log.info("Password for trainee with ID: {} updated successfully", traineeId);
    }

    @Override
    public void activateTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        log.info("Activating trainee with ID: {}", traineeId);

        Trainee trainee = traineeDao.findById(traineeId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", traineeId));

        if (trainee.getUser().getIsActive()) {
            throw new IllegalStateException("Trainee is already active");
        }

        User updatedUser = trainee.getUser().toBuilder()
                .isActive(true)
                .build();

        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);

        log.info("Trainee with ID: {} activated successfully", traineeId);
    }

    @Override
    public void deactivateTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        log.info("Deactivating trainee with ID: {}", traineeId);

        Trainee trainee = traineeDao.findById(traineeId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", traineeId));

        if (!trainee.getUser().getIsActive()) {
            throw new IllegalStateException("Trainee is already deactivated");
        }

        User updatedUser = trainee.getUser().toBuilder()
                .isActive(false)
                .build();

        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);

        log.info("Trainee with ID: {} deactivated successfully", traineeId);
    }

    @Override
    public boolean deleteTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        log.info("Deleting trainee with ID: {}", traineeId);

        Optional<Trainee> traineeOpt = traineeDao.findById(traineeId);
        if (traineeOpt.isEmpty()) {
            log.warn("Trainee with ID: {} not found for deletion", traineeId);
            return false;
        }

        Trainee trainee = traineeOpt.get();
        boolean deleted = traineeDao.deleteByUsername(trainee.getUser().getUsername());
        if (!deleted) {
            log.warn("Trainee with ID: {} not found for deletion", traineeId);
            return false;
        }

        log.info("Trainee with ID: {} deleted successfully", traineeId);
        return true;
    }

    @Override
    public boolean deleteTraineeByUsername(String username) {
        Objects.requireNonNull(username, "Trainee username cannot be null");
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
