package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TraineeDaoImpl implements TraineeDao {

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
                        """
                                SELECT t FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainers tr
                                LEFT JOIN FETCH tr.user LEFT JOIN FETCH tr.specialization
                                WHERE t.id = :id""",
                        Trainee.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        Objects.requireNonNull(username, "Trainee username cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery(
                        """
                                SELECT t FROM Trainee t JOIN FETCH t.user u LEFT JOIN FETCH t.trainers tr
                                LEFT JOIN FETCH tr.user LEFT JOIN FETCH tr.specialization
                                WHERE u.username = :username""",
                        Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional());
    }

    @Override
    public List<Trainee> findAll() {
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("""
                        SELECT t FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainers tr
                        LEFT JOIN FETCH tr.user LEFT JOIN FETCH tr.specialization""", Trainee.class)
                .list());
    }

    @Override
    public List<Trainer> findTraineeAvailableTrainers(String traineeUsername) {
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("""
                                SELECT t FROM Trainer t LEFT JOIN FETCH t.user LEFT JOIN FETCH t.specialization
                                WHERE t.id NOT IN (SELECT tr.id FROM Trainee te JOIN te.trainers tr WHERE te.user.username = :username)
                                """,
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList());
    }

    @Override
    public List<Trainer> findTraineeTrainersByUsernames(List<String> trainerUsernames) {
        if (trainerUsernames == null || trainerUsernames.isEmpty()) {
            return List.of();
        }
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT t FROM Trainer t JOIN FETCH t.user WHERE t.user.username IN (:usernames)", Trainer.class)
                .setParameter("usernames", trainerUsernames)
                .getResultList());
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
                            """
                                    SELECT t FROM Trainee t JOIN FETCH t.user LEFT JOIN FETCH t.trainers tr
                                    LEFT JOIN FETCH tr.user LEFT JOIN FETCH tr.specialization
                                    WHERE t.id = :id""",
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
