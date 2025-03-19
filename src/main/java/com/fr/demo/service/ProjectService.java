package com.fr.demo.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.model.entity.Project;
import com.fr.demo.model.enums.ProjectEventType;
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

    public ProjectDto convertToDto(Project project, ProjectEventType eventType) {
        return new ProjectDto(project.getId(), project.getName(), project.getType(), eventType);
    }

    public ProjectDto saveProject(ProjectDto projectDto) {
        return convertToDto(projectDao.save(convertToEntity(projectDto)), ProjectEventType.CREATED);
    }

    public List<ProjectType> getProjectTypes() {
        return Arrays.asList(ProjectType.values());
    }

    public List<ProjectDto> getAllProjects() {
        return StreamSupport.stream(projectDao.findAll()
                .spliterator(), false)
                .map(x -> convertToDto(x, null))
                .collect(Collectors.toList());
    }

    public ProjectDto findProjectById(Integer id) {
        return projectDao.findById(id)
                .map(x -> convertToDto(x, null))
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public List<ProjectDto> getProjectByType(ProjectType projectType) {
        return StreamSupport.stream(projectDao.findByType(projectType).spliterator(), false)
                .map(x -> convertToDto(x, null))
                .collect(Collectors.toList());
    }

    public ProjectDto updateProject(Integer id, ProjectDto projectDto) {
        return projectDao.findById(id)
                .map(project -> {
                    project.setName(projectDto.getName());
                    project.setType(projectDto.getType());
                    return convertToDto(projectDao.save(project), ProjectEventType.UPDATED);
                })
                .orElseThrow(() -> new ProjectNotFoundException(id));

    }
}
