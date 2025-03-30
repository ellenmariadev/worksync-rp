package com.example.worksync.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.example.worksync.dto.requests.ProjectDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.model.Project;
import com.example.worksync.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectDTO> listProjects(String title) {
        List<Project> projects;
        if (title != null && !title.isEmpty()) {
            projects = projectRepository.findByTitleContainingIgnoreCase(title);
        } else {
            projects = projectRepository.findAll();
        }
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ProjectDTO> findById(Long id) {
        return projectRepository.findById(id).map(this::convertToDTO);
    }

    public ProjectDTO createProject(ProjectDTO dto) {
        Project project = convertToEntity(dto);
        project = projectRepository.save(project);
        return convertToDTO(project);
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found!"));

        if (dto.getTitle() != null) {
            existingProject.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            existingProject.setDescription(dto.getDescription());
        }
        if (dto.getParticipantIds() != null) {
            existingProject.setParticipantIds(new ArrayList<>(dto.getParticipantIds()));
        }
        if (dto.getTaskIds() != null) {
            existingProject.setTaskIds(new ArrayList<>(dto.getTaskIds()));
        }

        existingProject = projectRepository.save(existingProject);
        return convertToDTO(existingProject);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found!");
        }
        projectRepository.deleteById(id);
    }

    public ProjectDTO addParticipantToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        // Criando uma nova lista mutável para evitar UnsupportedOperationException
        List<Long> participants = new ArrayList<>(project.getParticipantIds());

        if (!participants.contains(userId)) {
            participants.add(userId);
            project.setParticipantIds(participants); // Atualiza a lista no objeto
            project = projectRepository.save(project);
        } else {
            throw new RuntimeException("User is already a participant of this project!");
        }

        return convertToDTO(project);
    }

    private ProjectDTO convertToDTO(Project project) {
        return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getParticipantIds() != null ? new ArrayList<>(project.getParticipantIds()) : new ArrayList<>(),
                project.getTaskIds() != null ? new ArrayList<>(project.getTaskIds()) : new ArrayList<>()
        );
    }

    private Project convertToEntity(ProjectDTO dto) {
        Project project = new Project();
        project.setId(dto.getId());
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());

        if (dto.getParticipantIds() != null) {
            project.setParticipantIds(new ArrayList<>(dto.getParticipantIds()));
        } else {
            project.setParticipantIds(new ArrayList<>());
        }

        if (dto.getTaskIds() != null) {
            project.setTaskIds(new ArrayList<>(dto.getTaskIds()));
        } else {
            project.setTaskIds(new ArrayList<>());
        }

        return project;
    }
}