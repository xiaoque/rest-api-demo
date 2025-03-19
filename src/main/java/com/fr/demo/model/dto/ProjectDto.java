package com.fr.demo.model.dto;

import com.fr.demo.model.enums.ProjectEventType;
import com.fr.demo.model.enums.ProjectType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectDto {
    private Integer id;
    private String name;
    private ProjectType type;
    private ProjectEventType eventType;
}
