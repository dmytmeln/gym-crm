package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {

    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerDao.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        if (trainerDao.findById(trainer.getUserId()).isEmpty()) {
            throw new EntityNotFoundException("Trainer", trainer.getUserId());
        }

        return trainerDao.update(trainer);
    }

    public Trainer getTrainer(Long trainerId) {
        return trainerDao.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainerId));
    }

    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

}
