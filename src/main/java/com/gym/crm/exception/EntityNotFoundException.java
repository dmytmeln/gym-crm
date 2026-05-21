package com.gym.crm.exception;

import com.gym.crm.entity.EntityType;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends ServiceException {

    private static final String ERROR_MESSAGE_TEMPLATE = "%s not found with %s: %s";

    private final EntityType entityType;
    private final String key;
    private final Object value;

    public EntityNotFoundException(EntityType entityType, String key, Object value) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, entityType.getName(), key, value));
        this.entityType = entityType;
        this.key = key;
        this.value = value;
    }

    public static EntityNotFoundException forUsername(EntityType entityType, String username) {
        return new EntityNotFoundException(entityType, "username", username);
    }

    public static EntityNotFoundException forName(EntityType entityType, String name) {
        return new EntityNotFoundException(entityType, "name", name);
    }

}
