package com.fr.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private KafkaTemplate<String, ProjectDto> projectKafkaTemplate;

    @MockBean
    private KafkaTemplate<String, String> stringKafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = new ProjectDto();
        projectDto.setName("Test Project");
        projectDto.setType(ProjectType.CROCHET);
    }

    @Test
    void saveProject_ShouldReturnCreatedAndSendKafkaMessage() throws Exception {
        when(projectService.saveProject(any(ProjectDto.class))).thenReturn(projectDto);

        CompletableFuture<SendResult<String, ProjectDto>> future = CompletableFuture
                .completedFuture(mock(SendResult.class));
        when(projectKafkaTemplate.send(eq("project.created"), any(ProjectDto.class))).thenReturn(future);

        mockMvc.perform(post("/projects/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.type").value("CROCHET"));

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProjectDto> messageCaptor = ArgumentCaptor.forClass(ProjectDto.class);
        verify(projectKafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("project.created");
        assertThat(messageCaptor.getValue()).isEqualTo(projectDto);
    }

    @Test
    void getAllProjects_ShouldReturnProjectsAndSendAccessLog() throws Exception {
        List<ProjectDto> projects = Arrays.asList(projectDto);
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/projects/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CROCHET"))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(stringKafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("project.api.access");
        assertThat(messageCaptor.getValue()).isEqualTo("getAll projects endpoint accessed");
    }

    @Test
    void getProjectTypes_ShouldReturnAllEnumValues() throws Exception {
        List<ProjectType> types = Arrays.asList(ProjectType.values());
        when(projectService.getProjectTypes()).thenReturn(types);

        mockMvc.perform(get("/projects/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(types.size()))
                .andExpect(jsonPath("$[0]").value(ProjectType.CROCHET.toString()));
    }

    @Test
    void getProjectById_ShouldReturnProject() throws Exception {
        when(projectService.findProjectById(1)).thenReturn(projectDto);

        mockMvc.perform(get("/projects/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CROCHET"))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void getProjectsByType_ShouldReturnFilteredProjects() throws Exception {
        List<ProjectDto> projects = Arrays.asList(projectDto);
        when(projectService.getProjectByType(ProjectType.CROCHET)).thenReturn(projects);

        mockMvc.perform(get("/projects/type/CROCHET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CROCHET"));
    }

}