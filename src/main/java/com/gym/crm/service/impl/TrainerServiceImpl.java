package com.gym.crm.service.impl;

import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.exception.ConflictException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.repository.specification.TrainerTrainingCriteriaBuilder;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gym.crm.entity.EntityType.TRAINER;
import static com.gym.crm.entity.EntityType.TRAINING_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final String USERNAME_NULL_MSG = "Trainer username cannot be null";

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileCredentialGenerator credentialGenerator;
    private final PasswordEncoder passwordEncoder;
    private final TrainerTrainingCriteriaBuilder trainingCriteriaBuilder;

    @Override
    @Transactional
    public Trainer createTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        Objects.requireNonNull(trainer.getUser(), "Trainer user cannot be null");
        log.info("Creating trainer: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        String trainingTypeName = trainer.getSpecialization().getTrainingTypeName();
        TrainingType trainerSpecialization = trainingTypeRepository.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> EntityNotFoundException.forName(TRAINING_TYPE, trainingTypeName));

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

        Trainer createdTrainer = trainerRepository.save(trainerWithCredentialsAndSpecialization);
        log.info("Trainer created with ID: {} and username: {}",
                createdTrainer.getId(), createdTrainer.getUser().getUsername());

        User userWithRawPassword = createdTrainer.getUser().toBuilder()
                .password(password)
                .build();
        return createdTrainer.toBuilder()
                .user(userWithRawPassword)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainerByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        return trainerRepository.findByUsernameWithUserAndTraineesDetails(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINER, username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(TrainerTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        log.info("Getting trainings for trainer with username: {}", filter.getUsername());

        return trainingRepository.findAll(trainingCriteriaBuilder.build(filter));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        Objects.requireNonNull(password, "Password cannot be null");
        log.info("Checking if username and password match for trainer username: {}", username);

        Optional<Trainer> trainerOptional = trainerRepository.findByUsernameWithUser(username);
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
    @Transactional
    public Trainer updateTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        Objects.requireNonNull(trainer.getUser(), "Trainer user cannot be null");
        Objects.requireNonNull(trainer.getUser().getUsername(), USERNAME_NULL_MSG);
        log.info("Updating trainer with username: {}", trainer.getUser().getUsername());

        Trainer existingTrainer = trainerRepository.findByUsernameWithUserAndTraineesDetails(trainer.getUser().getUsername())
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINER, trainer.getUser().getUsername()));

        User updatedUser = existingTrainer.getUser().toBuilder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .isActive(trainer.getUser().getIsActive())
                .build();
        Trainer mergedTrainer = existingTrainer.toBuilder()
                .user(updatedUser)
                .build();

        Trainer updatedTrainer = trainerRepository.save(mergedTrainer);
        log.info("Trainer with ID: {} and username: {} updated successfully",
                updatedTrainer.getId(), updatedTrainer.getUser().getUsername());

        return updatedTrainer;
    }

    @Override
    @Transactional
    public void updateTrainerPassword(LoginChangeDto loginChangeDto) {
        Objects.requireNonNull(loginChangeDto, "LoginChangeDto cannot be null");
        log.info("Updating password for trainer with username: {}", loginChangeDto.username());

        Trainer trainer = trainerRepository.findByUsernameWithUser(loginChangeDto.username())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(loginChangeDto.oldPassword(), trainer.getUser().getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        User updatedUser = trainer.getUser().toBuilder()
                .password(passwordEncoder.encode(loginChangeDto.newPassword()))
                .build();
        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerRepository.save(updatedTrainer);
        log.info("Password for trainer with username: {} updated successfully", loginChangeDto.username());
    }

    @Override
    @Transactional
    public void updateActivationStatus(String username, boolean isActive) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);
        log.info("Updating activation status for trainer with username: {} to {}", username, isActive);

        Trainer trainer = trainerRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINER, username));

        if (Objects.equals(trainer.getUser().getIsActive(), isActive)) {
            throw new ConflictException(String.format("Trainer with username: %s is already %s", username, isActive ? "active" : "inactive"));
        }

        User updatedUser = trainer.getUser().toBuilder()
                .isActive(isActive)
                .build();
        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerRepository.save(updatedTrainer);
        log.info("Activation status for trainer with username: {} updated successfully", username);
    }

}
