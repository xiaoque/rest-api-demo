package com.fr.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fr.demo.model.enums.ProjectEventType;
import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.exception.ProjectNotFoundException;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.model.entity.Project;
import com.fr.demo.repository.ProjectDao;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectDao projectDao;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {

        project = new Project();
        project.setId(1);
        project.setName("Test Project");
        project.setType(ProjectType.CROCHET);

        projectDto = new ProjectDto();
        projectDto.setName("Test Project");
        projectDto.setType(ProjectType.CROCHET);
    }

    @Test
    void saveProject_ValidProject_ReturnSavedProject() {
        when(projectDao.save(any(Project.class))).thenReturn(project);
        ProjectDto resultProjectDto = projectService.saveProject(projectDto);

        assertNotNull(resultProjectDto);
        assertEquals(project.getName(), resultProjectDto.getName());
        assertEquals(project.getType(), resultProjectDto.getType());
        verify(projectDao, times(1)).save(any(Project.class));
    }

    @Test
    void getAll_ShouldReturnAllProjects() {
        when(projectDao.findAll()).thenReturn(Arrays.asList(project));
        List<ProjectDto> result = projectService.getAllProjects();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        ProjectDto resultProjectDto = result.iterator().next();
        assertEquals("Test Project", resultProjectDto.getName());
        assertEquals(ProjectType.CROCHET, resultProjectDto.getType());
        verify(projectDao, times(1)).findAll();
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenProjectExists() {
        when(projectDao.findById(1)).thenReturn(Optional.of(project));

        ProjectDto resultProjectDto = projectService.findProjectById(1);

        assertNotNull(resultProjectDto);
        assertEquals(1, resultProjectDto.getId());
        assertEquals(project.getName(), resultProjectDto.getName());
        assertEquals(project.getType(), resultProjectDto.getType());
        verify(projectDao, times(1)).findById(1);

    }

    @Test
    void getProjectById_ShouldThrowProjectNotFoundException_WhenProjectNotExist() {
        Integer projectId = 1;
        when(projectDao.findById(projectId)).thenReturn(Optional.empty());
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.findProjectById(projectId);
        });
        assertTrue(exception.getMessage().contains(String.valueOf(1)));
        verify(projectDao, times(1)).findById(projectId);
    }

    @Test
    void updaProject_ShouldReturnUpdatedProject_WhenProjectExists() {
        Integer projectId = 1;
        ProjectDto updatedProjectDto = new ProjectDto(projectId, "Updated name", ProjectType.TEST,
                ProjectEventType.UPDATED);

        when(projectDao.findById(projectId)).thenReturn(Optional.of(project));
        when(projectDao.save(any(Project.class))).thenAnswer(response -> response.getArgument(0));

        ProjectDto resultProjectDto = projectService.updateProject(projectId, updatedProjectDto);

        assertNotNull(resultProjectDto);
        assertEquals(projectId, resultProjectDto.getId());
        assertEquals(updatedProjectDto.getName(), resultProjectDto.getName());
        assertEquals(updatedProjectDto.getType(), resultProjectDto.getType());

        verify(projectDao, times(1)).findById(projectId);
        verify(projectDao, times(1)).save(project);
    }

    @Test
    void updatedProjectDto_ShouldReturnProjectNotFoundException_WhenProjectNotExist() {
        Integer projectId = 1;
        ProjectDto updatedProjectDto = new ProjectDto(projectId, "Updated name", ProjectType.TEST,
                ProjectEventType.UPDATED);

        when(projectDao.findById(projectId)).thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.updateProject(projectId, updatedProjectDto);
        });

        assertTrue(exception.getMessage().contains(String.valueOf(1)));

        verify(projectDao, times(1)).findById(projectId);
        verify(projectDao, never()).save(any(Project.class));
    }
}
