package com.gym.crm.storage;

import java.util.List;
import java.util.Optional;

public interface Storage {

    <T> void save(Namespace<T> namespace, Long id, T entity);

    <T> Optional<T> findById(Namespace<T> namespace, Long id);

    <T> List<T> findAll(Namespace<T> namespace);

    <T> boolean delete(Namespace<T> namespace, Long id);

}
