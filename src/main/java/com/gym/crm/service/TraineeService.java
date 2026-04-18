package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeService {

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public Trainee createTrainee(Trainee trainee) {
        return traineeDao.create(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        if (traineeDao.findById(trainee.getUserId()).isEmpty()) {
            throw new EntityNotFoundException("Trainee", trainee.getUserId());
        }

        return traineeDao.update(trainee);
    }

    public boolean deleteTrainee(Long traineeId) {
        return traineeDao.delete(traineeId);
    }

    public Trainee getTrainee(Long traineeId) {
        return traineeDao.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", traineeId));
    }

    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

}
