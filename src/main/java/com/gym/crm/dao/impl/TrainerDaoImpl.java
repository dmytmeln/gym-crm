package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerDaoImpl implements TrainerDao {

    private final TransactionManager transactionManager;

    @Override
    public Trainer save(Trainer entity) {
        Objects.requireNonNull(entity, "Trainer cannot be null");
        Objects.requireNonNull(entity.getUser(), "User cannot be null");

        if (entity.getId() != null) {
            throw new IllegalArgumentException("Trainer ID must be null for creation.");
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
    public Optional<Trainer> findById(Long id) {
        Objects.requireNonNull(id, "Trainer ID cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("""
                                SELECT t FROM Trainer t
                                LEFT JOIN FETCH t.user LEFT JOIN FETCH t.specialization
                                WHERE t.id = :id
                                """,
                        Trainer.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        Objects.requireNonNull(username, "Trainer username cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("""
                                SELECT t FROM Trainer t
                                LEFT JOIN FETCH t.user u LEFT JOIN FETCH t.specialization LEFT JOIN FETCH t.trainees tr LEFT JOIN FETCH tr.user
                                WHERE u.username = :username
                                """,
                        Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional());
    }

    @Override
    public List<Trainer> findAll() {
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT t FROM Trainer t LEFT JOIN FETCH t.user LEFT JOIN FETCH t.specialization", Trainer.class)
                .list());
    }

    @Override
    public Trainer update(Trainer entity) {
        Objects.requireNonNull(entity, "Trainer cannot be null");
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }

        return transactionManager.executeReturningWithinTx(session -> {
            session.merge(entity.getUser());
            Trainer merged = session.merge(entity);

            return session.createQuery("""
                                    SELECT t FROM Trainer t
                                    LEFT JOIN FETCH t.user LEFT JOIN FETCH t.specialization LEFT JOIN FETCH t.trainees tr LEFT JOIN FETCH tr.user
                                    WHERE t.id = :id
                                    """,
                            Trainer.class)
                    .setParameter("id", merged.getId())
                    .uniqueResult();
        });
    }

}
