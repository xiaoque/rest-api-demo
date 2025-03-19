package com.fr.demo.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fr.demo.event.cache.ProjectCache;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.model.enums.ProjectEventType;

@Service
public class ProjectEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ProjectEventListener.class);

    @Autowired
    private ProjectCache projectCache;

    @KafkaListener(topics = { ProjectEventType.CREATED_TOPIC,
            ProjectEventType.UPDATED_TOPIC,
            ProjectEventType.DELETED_TOPIC }, groupId = "project-events")
    public void handleProjectEvent(ProjectDto project) {
        logger.info("Received project event: {}", project);
        projectCache.clearCache(); // Invalidate the cache

        // Optionaly add the project to a cached list.
        if (ProjectEventType.CREATED.equals(project.getEventType())
                || ProjectEventType.UPDATED.equals(project.getEventType())) {
            projectCache.addProject(project);
            logger.info("Project added/updated in cache.");
        } else if (ProjectEventType.DELETED.equals(project.getEventType())) {
            projectCache.clearCache();
            logger.info("Project cache cleared.");
        }

        logger.info("Project cache cleared or updated.");
    }
}
