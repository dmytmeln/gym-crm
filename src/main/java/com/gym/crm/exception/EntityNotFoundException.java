package com.gym.crm.exception;

import com.gym.crm.entity.EntityType;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends ServiceException {

    private static final String ERROR_MESSAGE_TEMPLATE = "%s not found with %s: %s";

    private final EntityType entityType;
    private final String key;
    private final Object entityKey;

    public EntityNotFoundException(EntityType entityType, String key, Object entityKey) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, entityType.getName(), key, entityKey));
        this.entityType = entityType;
        this.key = key;
        this.entityKey = entityKey;
    }

    public static EntityNotFoundException forId(EntityType entityType, Long id) {
        return new EntityNotFoundException(entityType, "id", id);
    }

    public static EntityNotFoundException forUsername(EntityType entityType, String username) {
        return new EntityNotFoundException(entityType, "username", username);
    }

    public static EntityNotFoundException forName(EntityType entityType, String name) {
        return new EntityNotFoundException(entityType, "name", name);
    }

}
