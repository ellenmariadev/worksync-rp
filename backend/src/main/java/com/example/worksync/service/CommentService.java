package com.example.worksync.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.worksync.dto.requests.CommentDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.exceptions.ResourceNotFoundException;
import com.example.worksync.exceptions.UnauthorizedAccessException;
import com.example.worksync.model.Comment;
 import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.repository.CommentRepository;
import com.example.worksync.repository.TaskRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    public List<CommentDTO> listCommentsByTask(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO addComment(CommentDTO commentDTO, User user) {
        Optional<Task> taskOpt = taskRepository.findById(commentDTO.getTaskId());

        if (taskOpt.isEmpty()) {
            throw new NotFoundException("Task not found!");
        }

        Comment comment = new Comment();
        comment.setDescription(commentDTO.getDescription());
        comment.setTask(taskOpt.get());
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return convertToDTO(comment);
    }

    public CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getDescription(),
                comment.getTask().getId(),
                comment.getUser().getId(),
                comment.getCreatedAt()
        );
    }

    public void deleteComment(Long commentId, User user) throws ResourceNotFoundException {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
    
        if (commentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Comment not found", commentId);
        }
    
        Comment comment = commentOpt.get();
    
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this comment.");
        }
    
        commentRepository.delete(comment);
    }    
}