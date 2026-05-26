package com.gym.crm.transaction;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component("gymTransactionManager")
@RequiredArgsConstructor
public class TransactionManager {

    private final SessionFactory sessionFactory;

    public <T> T executeReturningWithinTx(Function<Session, T> sessionFunction) {
        Session session = sessionFactory.getCurrentSession();

        boolean isNewTransaction = !session.getTransaction().isActive();
        if (isNewTransaction) {
            session.getTransaction().begin();
        }

        try {
            T result = sessionFunction.apply(session);

            if (isNewTransaction) {
                session.getTransaction().commit();
            }

            return result;
        } catch (Exception e) {
            if (isNewTransaction && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }

            throw e;
        }
    }

    public void executeWithinTx(Consumer<Session> sessionConsumer) {
        executeReturningWithinTx(session -> {
            sessionConsumer.accept(session);
            return null;
        });
    }

    public <T> T executeReturningWithinTx(Supplier<T> supplier) {
        return executeReturningWithinTx(session -> supplier.get());
    }

}
