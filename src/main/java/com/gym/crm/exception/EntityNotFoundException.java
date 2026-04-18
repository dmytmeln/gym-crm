package com.gym.crm.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends ServiceException {

    private static final String ERROR_MESSAGE_TEMPLATE = "%s not found with id: %s";

    private final String entityType;
    private final Object entityId;

    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

}
