package com.gym.crm.storage.impl;

import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.NamespaceStorage;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryStorage implements Storage {

    private Map<Namespace<?>, NamespaceStorage<?>> storageMap;

    @Autowired
    public void setNamespaceStorages(List<NamespaceStorage<?>> namespaceStorages) {
        this.storageMap = new HashMap<>();
        for (NamespaceStorage<?> storage : namespaceStorages) {
            storageMap.put(storage.getNamespace(), storage);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void save(Namespace<T> namespace, Long id, T entity) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        NamespaceStorage<T> storage = (NamespaceStorage<T>) storageMap.get(namespace);
        storage.save(id, entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> findById(Namespace<T> namespace, Long id) {
        NamespaceStorage<T> storage = (NamespaceStorage<T>) storageMap.get(namespace);
        return storage.findById(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Namespace<T> namespace) {
        NamespaceStorage<T> storage = (NamespaceStorage<T>) storageMap.get(namespace);
        return storage.findAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean delete(Namespace<T> namespace, Long id) {
        NamespaceStorage<T> storage = (NamespaceStorage<T>) storageMap.get(namespace);
        return storage.delete(id);
    }

}
