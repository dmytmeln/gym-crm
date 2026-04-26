package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.helper.ProfileCredentialGenerator;
import com.gym.crm.service.TrainerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;
    private ProfileCredentialGenerator credentialGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setCredentialGenerator(ProfileCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        String username = credentialGenerator.generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = credentialGenerator.generatePassword();

        Trainer trainerWithCredentials = trainer.toBuilder()
                .username(username)
                .password(password)
                .build();

        Trainer createdTrainer = trainerDao.create(trainerWithCredentials);
        log.info("Trainer created with ID: {} and username: {}",
                createdTrainer.getUserId(), createdTrainer.getUsername());

        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer cannot be null");
        log.info("Updating trainer with ID: {}", trainer.getUserId());

        Trainer existingTrainer = trainerDao.findById(trainer.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainer.getUserId()));

        Trainer mergedTrainer = existingTrainer.toBuilder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .password(trainer.getPassword())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .build();

        Trainer updatedTrainer = trainerDao.update(mergedTrainer);
        log.info("Trainer with ID: {} and username: {} updated successfully",
                updatedTrainer.getUserId(), updatedTrainer.getUsername());

        return updatedTrainer;
    }

    @Override
    public Trainer getTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");

        return trainerDao.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainerId));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

}
