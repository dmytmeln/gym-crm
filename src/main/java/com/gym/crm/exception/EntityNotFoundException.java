package com.gym.crm.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends ServiceException {

    private static final String ERROR_MESSAGE_TEMPLATE = "%s not found with %s: %s";

    private final String entityType;
    private final String key;
    private final Object entityKey;

    public EntityNotFoundException(String entityType, String key, Object entityKey) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, entityType, key, entityKey));
        this.entityType = entityType;
        this.key = key;
        this.entityKey = entityKey;
    }

    public static EntityNotFoundException forId(String entityType, Long id) {
        return new EntityNotFoundException(entityType, "id", id);
    }

    public static EntityNotFoundException forUsername(String entityType, String username) {
        return new EntityNotFoundException(entityType, "username", username);
    }

    public static EntityNotFoundException forName(String entityType, String name) {
        return new EntityNotFoundException(entityType, "name", name);
    }

}
