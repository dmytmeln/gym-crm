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
    public <T> T save(Namespace<T> namespace, T entity) {
        NamespaceStorage<T> storage = getNamespaceStorage(namespace);
        return storage.save(entity);
    }

    @Override
    public <T> void update(Namespace<T> namespace, Long id, T entity) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        NamespaceStorage<T> storage = getNamespaceStorage(namespace);
        storage.update(id, entity);
    }

    @Override
    public <T> Optional<T> findById(Namespace<T> namespace, Long id) {
        NamespaceStorage<T> storage = getNamespaceStorage(namespace);
        return storage.findById(id);
    }

    @Override
    public <T> List<T> findAll(Namespace<T> namespace) {
        NamespaceStorage<T> storage = getNamespaceStorage(namespace);
        return storage.findAll();
    }

    @Override
    public <T> boolean delete(Namespace<T> namespace, Long id) {
        NamespaceStorage<T> storage = getNamespaceStorage(namespace);
        return storage.delete(id);
    }

    @SuppressWarnings("unchecked")
    private <T> NamespaceStorage<T> getNamespaceStorage(Namespace<T> namespace) {
        return (NamespaceStorage<T>) storageMap.get(namespace);
    }

}