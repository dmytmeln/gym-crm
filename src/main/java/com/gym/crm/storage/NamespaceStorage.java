package com.gym.crm.storage;

import java.util.List;
import java.util.Optional;

public interface NamespaceStorage<T> {

    Namespace<T> getNamespace();

    T save(T entity);

    void update(Long id, T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    boolean delete(Long id);

}
