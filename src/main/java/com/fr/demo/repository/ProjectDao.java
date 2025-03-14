package com.fr.demo.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import com.fr.demo.model.enums.ProjectType;
import com.fr.demo.model.entity.Project;

public interface ProjectDao extends CrudRepository<Project, Integer> {

    List<Project> findByType(ProjectType type);

    Optional<Project> findById(Integer id);
}
