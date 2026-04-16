package com.gym.crm.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    T create(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    T update(T entity);

    boolean delete(Long id);

}
