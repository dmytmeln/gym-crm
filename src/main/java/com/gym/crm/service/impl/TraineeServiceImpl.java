package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.generator.ProfileCredentialGenerator;
import com.gym.crm.service.TraineeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private ProfileCredentialGenerator credentialGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setCredentialGenerator(ProfileCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee cannot be null");
        log.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        String username = credentialGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = credentialGenerator.generatePassword();

        Trainee traineeWithCredentials = trainee.toBuilder()
                .username(username)
                .password(password)
                .build();

        Trainee createdTrainee = traineeDao.create(traineeWithCredentials);
        log.info("Trainee created with ID: {} and username: {}", createdTrainee.getUserId(), createdTrainee.getUsername());

        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee cannot be null");
        log.info("Updating trainee with ID: {}", trainee.getUserId());

        Trainee existingTrainee = traineeDao.findById(trainee.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee", trainee.getUserId()));

        Trainee mergedTrainee = existingTrainee.toBuilder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .password(trainee.getPassword())
                .isActive(trainee.isActive())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .build();

        Trainee updatedTrainee = traineeDao.update(mergedTrainee);
        log.info("Trainee with ID: {} and username: {} updated successfully",
                updatedTrainee.getUserId(), updatedTrainee.getUsername());

        return updatedTrainee;
    }

    @Override
    public boolean deleteTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        log.info("Deleting trainee with ID: {}", traineeId);

        boolean deleted = traineeDao.delete(traineeId);
        if (!deleted) {
            log.warn("Trainee with ID: {} not found for deletion", traineeId);
            return false;
        }

        log.info("Trainee with ID: {} deleted successfully", traineeId);
        return true;
    }

    @Override
    public Trainee getTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        return traineeDao.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", traineeId));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

}
