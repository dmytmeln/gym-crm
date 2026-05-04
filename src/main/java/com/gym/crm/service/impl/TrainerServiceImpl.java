package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeEntityDao;
import com.gym.crm.dao.TrainerEntityDao;
import com.gym.crm.dao.TrainingTypeEntityDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.helper.ProfileCredentialGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private TrainerEntityDao trainerDao;
    private TraineeEntityDao traineeDao;
    private TrainingTypeEntityDao trainingTypeDao;
    private ProfileCredentialGenerator credentialGenerator;

    @Autowired
    public void setTrainerDao(TrainerEntityDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeEntityDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setCredentialGenerator(ProfileCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeEntityDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        Objects.requireNonNull(trainer.getUser(), "Trainer user cannot be null");
        log.info("Creating trainer: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        TrainingType trainerSpecialization = trainingTypeDao.findById(trainer.getSpecialization().getId())
                .orElseThrow(() -> EntityNotFoundException.forId("TrainingType", trainer.getSpecialization().getId()));

        String username = credentialGenerator.generateUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        String password = credentialGenerator.generatePassword();

        User user = trainer.getUser().toBuilder()
                .username(username)
                .password(password)
                .build();

        Trainer trainerWithCredentialsAndSpecialization = trainer.toBuilder()
                .user(user)
                .specialization(trainerSpecialization)
                .build();

        Trainer createdTrainer = trainerDao.save(trainerWithCredentialsAndSpecialization);
        log.info("Trainer created with ID: {} and username: {}",
                createdTrainer.getId(), createdTrainer.getUser().getUsername());

        return createdTrainer;
    }

    @Override
    public Trainer getTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");

        return trainerDao.findById(trainerId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainer", trainerId));
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        Objects.requireNonNull(username, "Trainer username cannot be null");

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername("Trainer", username));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

    @Override
    public List<Trainer> getAllTrainersNotAssignedToTrainee(String traineeUsername) {
        Objects.requireNonNull(traineeUsername, "Trainee username cannot be null");
        log.info("Getting all trainers not assigned to trainee with username: {}", traineeUsername);

        if (!traineeExistsByUsername(traineeUsername)) {
            throw EntityNotFoundException.forUsername("Trainee", traineeUsername);
        }

        return trainerDao.findAllNotAssignedToTrainee(traineeUsername);
    }

    @Override
    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(password, "Password cannot be null");
        log.info("Checking if username and password match for trainer username: {}", username);

        Trainer trainer = getTrainerByUsername(username);

        boolean passwordsMatch = Objects.equals(trainer.getUser().getPassword(), password);
        if (!passwordsMatch) {
            log.warn("Passwords do not match for trainer username: {}", username);
            return false;
        }

        log.info("Passwords match for trainer username: {}", username);
        return true;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        Objects.requireNonNull(trainer.getUser(), "Trainer user cannot be null");
        Objects.requireNonNull(trainer.getId(), "Trainer ID cannot be null");
        log.info("Updating trainer with ID: {}", trainer.getId());

        Trainer existingTrainer = trainerDao.findById(trainer.getId())
                .orElseThrow(() -> EntityNotFoundException.forId("Trainer", trainer.getId()));

        boolean hasSpecializationChanged = !Objects.equals(existingTrainer.getSpecialization().getId(), trainer.getSpecialization().getId());
        if (hasSpecializationChanged) {
            TrainingType trainerSpecialization = trainingTypeDao.findById(trainer.getSpecialization().getId())
                    .orElseThrow(() -> EntityNotFoundException.forId("TrainingType", trainer.getSpecialization().getId()));
            existingTrainer.setSpecialization(trainerSpecialization);
        }

        User updatedUser = existingTrainer.getUser().toBuilder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .isActive(trainer.getUser().getIsActive())
                .build();

        Trainer mergedTrainer = existingTrainer.toBuilder()
                .user(updatedUser)
                .build();

        Trainer updatedTrainer = trainerDao.update(mergedTrainer);
        log.info("Trainer with ID: {} and username: {} updated successfully",
                updatedTrainer.getId(), updatedTrainer.getUser().getUsername());

        return updatedTrainer;
    }

    @Override
    public void updateTrainerPassword(Long trainerId, String newPassword) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");
        Objects.requireNonNull(newPassword, "Password cannot be null");
        log.info("Updating password for trainer with ID: {}", trainerId);

        Trainer trainer = trainerDao.findById(trainerId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainer", trainerId));

        User updatedUser = trainer.getUser().toBuilder()
                .password(newPassword)
                .build();

        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);

        log.info("Password for trainer with ID: {} updated successfully", trainerId);
    }

    @Override
    public void activateTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");
        log.info("Activating trainer with ID: {}", trainerId);

        Trainer trainer = trainerDao.findById(trainerId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainer", trainerId));

        if (trainer.getUser().getIsActive()) {
            throw new IllegalStateException("Trainer is already active");
        }

        User updatedUser = trainer.getUser().toBuilder()
                .isActive(true)
                .build();

        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);

        log.info("Trainer with ID: {} activated successfully", trainerId);
    }

    @Override
    public void deactivateTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");
        log.info("Deactivating trainer with ID: {}", trainerId);

        Trainer trainer = trainerDao.findById(trainerId)
                .orElseThrow(() -> EntityNotFoundException.forId("Trainer", trainerId));

        if (!trainer.getUser().getIsActive()) {
            throw new IllegalStateException("Trainer is already deactivated");
        }

        User updatedUser = trainer.getUser().toBuilder()
                .isActive(false)
                .build();

        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);

        log.info("Trainer with ID: {} deactivated successfully", trainerId);
    }

    private boolean traineeExistsByUsername(String username) {
        return traineeDao.findByUsername(username).isPresent();
    }

}
