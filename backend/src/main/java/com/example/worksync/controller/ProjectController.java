package com.example.worksync.controller;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Endpoint to create a new project
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO newProject = projectService.createProject(projectDTO);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    // Endpoint to list all projects
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> listProjects() {
        List<ProjectDTO> projects = projectService.listProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}
