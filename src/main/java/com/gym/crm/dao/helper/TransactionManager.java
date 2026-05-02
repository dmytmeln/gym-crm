package com.gym.crm.dao.helper;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TransactionManager {

    private final SessionFactory sessionFactory;

    public void executeWithinTx(Consumer<Session> sessionConsumer) {
        Session session = sessionFactory.openSession();

        try {
            session.getTransaction().begin();
            sessionConsumer.accept(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public <T> T executeReturningWithinTx(Function<Session, T> sessionFunction) {
        Session session = sessionFactory.openSession();

        try {
            session.getTransaction().begin();
            T result = sessionFunction.apply(session);
            session.getTransaction().commit();

            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
