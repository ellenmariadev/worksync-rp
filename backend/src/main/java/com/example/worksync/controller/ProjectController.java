package com.example.worksync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.service.ProjectService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects")

public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        if(projectDTO.getTitle() == null || projectDTO.getTitle().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ProjectDTO newProject = projectService.createProject(projectDTO);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> listProjects(@RequestParam(required = false) String title) {
        List<ProjectDTO> projects = projectService.listProjects(title);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }    

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/participants/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDTO> addParticipant(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectDTO updatedProject = projectService.addParticipantToProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }
}
