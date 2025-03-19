package com.fr.demo.event.cache;

import com.fr.demo.model.dto.ProjectDto;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Service
public class ProjectCache {
    private static final Logger logger = LoggerFactory.getLogger(ProjectCache.class);
    private List<ProjectDto> cachedProjects = null;

    public void setChachedProjects(List<ProjectDto> projects) {
        this.cachedProjects = new CopyOnWriteArrayList<>(projects);
    }

    public void clearCache() {
        this.cachedProjects = null;
        logger.info("Project cache cleared.");
    }

    public void addProject(ProjectDto project) {
        if (cachedProjects == null) {
            cachedProjects = new CopyOnWriteArrayList<>();
        }
        cachedProjects.add(project);
        logger.info("Project added to cach: {}", project);
    }
}
