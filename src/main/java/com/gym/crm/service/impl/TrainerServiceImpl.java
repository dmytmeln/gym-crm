package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.ProfileCredentialService;
import com.gym.crm.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;
    private ProfileCredentialService credentialService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setCredentialService(ProfileCredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        String username = credentialService.generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = credentialService.generatePassword();

        Trainer trainerWithCredentials = trainer.toBuilder()
                .username(username)
                .password(password)
                .build();

        return trainerDao.create(trainerWithCredentials);
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Trainer existingTrainer = trainerDao.findById(trainer.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainer.getUserId()));

        Trainer mergedTrainer = existingTrainer.toBuilder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .password(trainer.getPassword())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .build();

        return trainerDao.update(mergedTrainer);
    }

    @Override
    public Trainer getTrainer(Long trainerId) {
        return trainerDao.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainerId));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

}
