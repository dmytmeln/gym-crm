package com.gym.crm.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends ServiceException {

    private final String entityType;
    private final Object entityId;

    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format("%s not found with id: %s", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

}
