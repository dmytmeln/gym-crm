package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import com.gym.crm.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private static final String USERNAME_NULL_MSG = "Trainer username cannot be null";

    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private TrainingDao trainingDao;
    private ProfileCredentialGenerator credentialGenerator;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
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
    public Trainer createTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        Objects.requireNonNull(trainer.getUser(), "Trainer user cannot be null");
        log.info("Creating trainer: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        String trainingTypeName = trainer.getSpecialization().getTrainingTypeName();
        TrainingType trainerSpecialization = trainingTypeDao.findByName(trainingTypeName)
                .orElseThrow(() -> EntityNotFoundException.forName("TrainingType", trainingTypeName));

        String username = credentialGenerator.generateUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        String password = credentialGenerator.generatePassword();

        User user = trainer.getUser().toBuilder()
                .username(username)
                .password(passwordEncoder.encode(password))
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
    public Trainer getTrainerByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername("Trainer", username));
    }

    @Override
    public List<Training> getTrainerTrainings(TrainerTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        log.info("Getting trainings for trainer with username: {}", filter.getUsername());

        return trainingDao.findTrainerTrainingsByCriteria(filter);
    }

    @Override
    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        Objects.requireNonNull(password, "Password cannot be null");
        log.info("Checking if username and password match for trainer username: {}", username);

        Optional<Trainer> trainerOptional = trainerDao.findByUsername(username);
        if (trainerOptional.isEmpty()) {
            log.warn("Trainer not found with username: {}", username);
            return false;
        }

        Trainer trainer = trainerOptional.get();
        boolean passwordMatches = passwordEncoder.matches(password, trainer.getUser().getPassword());
        if (!passwordMatches) {
            log.warn("Passwords do not match for trainer username: {}", username);
            return false;
        }

        log.info("Passwords match for trainer username: {}", username);
        return true;
    }

    @Override
    @Transaction
    public Trainer updateTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        Objects.requireNonNull(trainer.getUser(), "Trainer user cannot be null");
        Objects.requireNonNull(trainer.getUser().getUsername(), USERNAME_NULL_MSG);
        log.info("Updating trainer with username: {}", trainer.getUser().getUsername());

        Trainer existingTrainer = trainerDao.findByUsername(trainer.getUser().getUsername())
                .orElseThrow(() -> EntityNotFoundException.forUsername("Trainer", trainer.getUser().getUsername()));

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
    public void updateTrainerPassword(LoginChangeDto loginChangeDto) {
        Objects.requireNonNull(loginChangeDto, "LoginChangeDto cannot be null");
        log.info("Updating password for trainer with username: {}", loginChangeDto.username());

        if (!doesUsernameAndPasswordMatch(loginChangeDto.username(), loginChangeDto.oldPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Trainer trainer = getTrainerByUsername(loginChangeDto.username());
        User updatedUser = trainer.getUser().toBuilder()
                .password(passwordEncoder.encode(loginChangeDto.newPassword()))
                .build();
        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);
        log.info("Password for trainer with username: {} updated successfully", loginChangeDto.username());
    }

    @Override
    public void updateActivationStatus(String username, boolean isActive) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Updating activation status for trainer with username: {} to {}", username, isActive);

        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername("Trainer", username));

        if (trainer.getUser().getIsActive() == isActive) {
            log.warn("Trainer with username: {} is already {}", username, isActive ? "active" : "inactive");
            return;
        }

        User updatedUser = trainer.getUser().toBuilder()
                .isActive(isActive)
                .build();
        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);
        log.info("Activation status for trainer with username: {} updated successfully", username);
    }

}
