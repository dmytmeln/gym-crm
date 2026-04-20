package com.gym.crm.storage.impl;

import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.NamespaceStorage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public abstract class InMemoryNamespaceStorage<T> implements NamespaceStorage<T> {

    private final Namespace<T> namespace;
    private final Map<Long, T> storage;
    private final AtomicLong idCounter = new AtomicLong(1);

    public InMemoryNamespaceStorage(Namespace<T> namespace) {
        this.namespace = namespace;
        this.storage = new HashMap<>();
    }

    protected abstract T setId(T entity, Long id);

    @Override
    public T save(T entity) {
        Long newId = idCounter.getAndIncrement();
        T entityWithId = setId(entity, newId);

        storage.put(newId, entityWithId);

        return entityWithId;
    }

    @Override
    public void update(Long id, T entity) {
        storage.put(id, entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(Long id) {
        return storage.remove(id) != null;
    }

    public void clear() {
        storage.clear();
        idCounter.set(1);
    }

}
