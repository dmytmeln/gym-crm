package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        return traineeDao.create(trainee);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        if (traineeDao.findById(trainee.getUserId()).isEmpty()) {
            throw new EntityNotFoundException("Trainee", trainee.getUserId());
        }

        return traineeDao.update(trainee);
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
