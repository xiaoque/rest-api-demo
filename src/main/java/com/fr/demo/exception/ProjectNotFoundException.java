package com.fr.demo.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Integer id) {
        super(String.format("Project not found with id %s", id));
    }
}
