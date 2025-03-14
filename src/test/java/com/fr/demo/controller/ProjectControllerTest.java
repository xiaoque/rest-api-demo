package com.fr.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.service.ProjectService;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService; // Mock the service

    @InjectMocks
    private ProjectController projectController; // Inject the mock into the controller

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectDto = new ProjectDto();
        projectDto.setName("Test Project");
        projectDto.setType(ProjectType.CROCHET);
    }

    @Test
    void saveProject_ValidProject_ReturnsCreated() throws Exception {
        // Arrange
        when(projectService.saveProject(any(ProjectDto.class))).thenReturn(projectDto);

        // Act & Assert
        mockMvc.perform(post("/projects/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Project\",\"type\":\"CROCHET\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.type").value("CROCHET"));
    }

    @Test
    void getAllProjects_ShouldReturnAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(projectDto));

        mockMvc.perform(get("/projects/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0],name").value("Test Project"));

    }

    @Test
    void getAllEnums_ShouldReturnAllProjectTypes() throws Exception {
        List<ProjectType> projectTypes = Arrays.asList(ProjectType.values());
        when(projectService.getProjectTypes()).thenReturn(projectTypes);

        mockMvc.perform(get("/projects/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(projectTypes.size()));
    }
}
