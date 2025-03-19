package com.fr.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.service.ProjectService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final static Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;
    @Autowired
    private KafkaTemplate<String, ProjectDto> projectKafkaTemplate; // For ProjectDto

    @Autowired
    private KafkaTemplate<String, String> stringKafkaTemplate; // For String events

    private static final String PROJECT_CREATED_TOPIC = "project.created";
    private static final String API_ACCESS_TOPIC = "project.api.access";

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/")
    public ResponseEntity<ProjectDto> saveProject(@RequestBody ProjectDto entity) {
        logger.info("Save project: {}", entity);
        ProjectDto newEntity = projectService.saveProject(entity);
        // Publish the created project to Kafka with error handling
        CompletableFuture<SendResult<String, ProjectDto>> future = projectKafkaTemplate.send(PROJECT_CREATED_TOPIC,
                newEntity);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[{}] with offset=[{}]", newEntity, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] due to : {}", newEntity, ex.getMessage());
            }
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(newEntity);
    }

    @GetMapping("/")
    public ResponseEntity<List<ProjectDto>> getAll() {
        logger.info("Fetching all project");
        List<ProjectDto> projectDtos = projectService.getAllProjects();
        stringKafkaTemplate.send(API_ACCESS_TOPIC, "getAll projects endpoint accessed");
        return ResponseEntity.ok(projectDtos);

    }

    @GetMapping("/types")
    public ResponseEntity<List<ProjectType>> getProjectTypes() {
        List<ProjectType> projectTypes = projectService.getProjectTypes();
        return ResponseEntity.ok(projectTypes);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable(name = "id") Integer id) {
        logger.info("Find project by id :{}", id);
        ProjectDto projectDto = projectService.findProjectById(id);
        return ResponseEntity.ok(projectDto);

    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProjectDto>> getProjectByType(@PathVariable(name = "type") ProjectType type) {
        logger.info("Find project by type: {} ", type);
        List<ProjectDto> projectDtos = projectService.getProjectByType(type);
        return ResponseEntity.ok(projectDtos);
    }

}
