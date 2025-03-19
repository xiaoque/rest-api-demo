package com.fr.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectEventType {
    CREATED("project.created"), UPDATED("project.updated"), DELETED("project.deleted");

    public static final String CREATED_TOPIC = "project.created";
    public static final String UPDATED_TOPIC = "project.updated";
    public static final String DELETED_TOPIC = "project.deleted";

    private final String eventType;

    @JsonCreator
    ProjectEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonValue
    public String getEventType() {
        return eventType;
    }
}
