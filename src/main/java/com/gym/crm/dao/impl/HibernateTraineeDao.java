package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeEntityDao;
import com.gym.crm.dao.helper.TransactionManager;
import com.gym.crm.entity.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HibernateTraineeDao implements TraineeEntityDao {

    private final TransactionManager transactionManager;

    @Override
    public Trainee save(Trainee entity) {
        Objects.requireNonNull(entity, "Trainee cannot be null");
        Objects.requireNonNull(entity.getUser(), "User cannot be null");

        if (entity.getId() != null) {
            throw new IllegalArgumentException("Trainee ID must be null for creation.");
        }
        if (entity.getUser().getId() != null) {
            throw new IllegalArgumentException("User ID must be null for creation.");
        }

        transactionManager.executeWithinTx(session -> {
            session.persist(entity.getUser());
            session.persist(entity);
        });

        return entity;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Objects.requireNonNull(id, "Trainee ID cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery(
                        "SELECT t FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainings LEFT JOIN FETCH t.trainers WHERE t.id = :id",
                        Trainee.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        Objects.requireNonNull(username, "Trainee username cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery(
                        "SELECT t FROM Trainee t JOIN FETCH t.user u LEFT JOIN FETCH t.trainings LEFT JOIN FETCH t.trainers WHERE u.username = :username",
                        Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional());
    }

    @Override
    public List<Trainee> findAll() {
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT t FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainings LEFT JOIN FETCH t.trainers", Trainee.class)
                .list());
    }

    @Override
    public Trainee update(Trainee entity) {
        Objects.requireNonNull(entity, "Trainee cannot be null");
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }

        return transactionManager.executeReturningWithinTx(session -> {
            session.merge(entity.getUser());
            Trainee merged = session.merge(entity);

            return session.createQuery(
                            "SELECT t FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainings LEFT JOIN FETCH t.trainers WHERE t.id = :id",
                            Trainee.class)
                    .setParameter("id", merged.getId())
                    .uniqueResult();
        });
    }

    @Override
    public boolean deleteByUsername(String username) {
        Objects.requireNonNull(username, "Trainee username cannot be null");

        return transactionManager.executeReturningWithinTx(session -> {
            Optional<Trainee> traineeOptional = session.createQuery(
                            "SELECT t FROM Trainee t JOIN FETCH t.user u WHERE u.username = :username", Trainee.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();

            if (traineeOptional.isEmpty()) {
                return false;
            }

            Trainee trainee = traineeOptional.get();
            session.remove(trainee);
            session.remove(trainee.getUser());

            return true;
        });
    }

}
