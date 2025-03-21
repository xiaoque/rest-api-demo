package com.fr.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.kafka.KafkaProducer;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.service.ProjectService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final static Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public ProjectController(ProjectService projectService, KafkaProducer kafkaProducer) {
        this.projectService = projectService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/")
    public ResponseEntity<ProjectDto> saveProject(@RequestBody ProjectDto entity) {
        logger.info("Save project: {}", entity);
        ProjectDto newEntity = projectService.saveProject(entity);
        kafkaProducer.sendProject(newEntity);
        logger.info("Project created and sent to Kafka", newEntity);
        return ResponseEntity.ok(newEntity);
    }

    @GetMapping("/")
    public ResponseEntity<List<ProjectDto>> getAll() {
        logger.info("Fetching all project");
        List<ProjectDto> projectDtos = new ArrayList<>();
        projectDtos.addAll(projectService.getAllProjects());
        kafkaProducer.sendProjectList(projectDtos);
        logger.info("Project list sent to Kafka");
        return ResponseEntity.ok(projectDtos);
    }

    @GetMapping("/types")
    public ResponseEntity<List<ProjectType>> getProjectTypes() {
        List<ProjectType> projectTypes = projectService.getProjectTypes();
        kafkaProducer.sendProjectTypes(projectTypes);
        logger.info("Project types sent to Kafka");
        return ResponseEntity.ok(projectTypes);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable(name = "id") Integer id) {
        logger.info("Find project by id :{}", id);
        ProjectDto projectDto = projectService.findProjectById(id);
        kafkaProducer.sendProject(projectDto);
        logger.info("Project found and sent to Kafka", projectDto);
        return ResponseEntity.ok(projectDto);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProjectDto>> getProjectByType(@PathVariable(name = "type") ProjectType type) {
        logger.info("Find project by type: {} ", type);
        List<ProjectDto> projectDtos = projectService.getProjectByType(type);
        kafkaProducer.sendProjectList(projectDtos);
        logger.info("Projects found and sent to Kafka");
        return ResponseEntity.ok(projectDtos);
    }

}
