package com.gym.crm.storage.impl;

import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.NamespaceStorage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class InMemoryNamespaceStorage<T> implements NamespaceStorage<T> {

    private final Namespace<T> namespace;
    private final Map<Long, T> storage;

    public InMemoryNamespaceStorage(Namespace<T> namespace) {
        this.namespace = namespace;
        this.storage = new HashMap<>();
    }

    @Override
    public void save(Long id, T entity) {
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

}
