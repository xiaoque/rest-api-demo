package com.fr.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.model.entity.Project;
import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.repository.ProjectDao;
import com.fr.demo.exception.ProjectNotFoundException;

@Service
public class ProjectService {
    private final ProjectDao projectDao;

    public ProjectService(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    public Project convertToEntity(ProjectDto projectDto) {
        return new Project(projectDto.getId(), projectDto.getName(), projectDto.getType());
    }

    public ProjectDto convertToDto(Project project) {
        return new ProjectDto(project.getId(), project.getName(), project.getType());
    }

    public ProjectDto saveProject(ProjectDto projectDto) {
        return convertToDto(projectDao.save(convertToEntity(projectDto)));
    }

    public List<ProjectType> getProjectTypes() {
        return Arrays.asList(ProjectType.values());
    }

    public List<ProjectDto> getAllProjects() {
        return StreamSupport.stream(projectDao.findAll()
                .spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProjectDto findProjectById(Integer id) {
        return projectDao.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public List<ProjectDto> getProjectByType(ProjectType projectType) {
        return StreamSupport.stream(projectDao.findByType(projectType).spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProjectDto updaProject(Integer id, ProjectDto projectDto) {
        return projectDao.findById(id)
                .map(project -> {
                    project.setName(projectDto.getName());
                    project.setType(projectDto.getType());
                    return convertToDto(projectDao.save(project));
                })
                .orElseThrow(() -> new ProjectNotFoundException(id));

    }
}
