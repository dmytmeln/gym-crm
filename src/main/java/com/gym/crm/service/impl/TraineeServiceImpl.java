package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.ProfileCredentialService;
import com.gym.crm.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private ProfileCredentialService credentialService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setCredentialService(ProfileCredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        String username = credentialService.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = credentialService.generatePassword();

        Trainee traineeWithCredentials = trainee.toBuilder()
                .username(username)
                .password(password)
                .build();

        return traineeDao.create(traineeWithCredentials);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
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

        return traineeDao.update(mergedTrainee);
    }

    @Override
    public boolean deleteTrainee(Long traineeId) {
        return traineeDao.delete(traineeId);
    }

    @Override
    public Trainee getTrainee(Long traineeId) {
        return traineeDao.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", traineeId));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

}
