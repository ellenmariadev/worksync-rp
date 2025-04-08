package com.example.worksync.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.worksync.dto.requests.CommentDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.exceptions.ResourceNotFoundException;
import com.example.worksync.exceptions.UnauthorizedAccessException;
import com.example.worksync.model.Comment;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.repository.CommentRepository;
import com.example.worksync.repository.TaskRepository;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private User anotherUser;
    private Task task;
    private CommentDTO commentDTO;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        anotherUser = new User();
        anotherUser.setId(2L);

        task = new Task();
        task.setId(1L);

        commentDTO = new CommentDTO(null, "This is a comment", task.getId(), user.getId(), LocalDateTime.now());
        comment = new Comment("This is a comment", task, user);
    }

    @Test
    void testAddComment_Success() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO newCommentDTO = commentService.addComment(commentDTO, user);

        assertNotNull(newCommentDTO);
        assertEquals("This is a comment", newCommentDTO.getDescription());
        assertEquals(user.getId(), newCommentDTO.getUserId());
    }

    @Test
    void testAddComment_TaskNotFound() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addComment(commentDTO, user));
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(comment.getId(), user);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_CommentNotFound_ThrowsException() {
        Long commentId = 1L;
        User newUser = new User();
        newUser.setId(1L);

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> commentService.deleteComment(commentId, newUser));
        
        assertEquals("Comment not found with ID " + newUser.getId() + " not found", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }

 
    @Test
    void deleteComment_UnauthorizedAccess() {
        Long commentId = 1L;
        User newUser = new User();
        user.setId(1L);

        User commentOwner = new User();
        commentOwner.setId(2L);

        Comment newComment = new Comment();
        newComment.setId(commentId);
        newComment.setUser(commentOwner);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(newComment));

        try {
            assertThrows(UnauthorizedAccessException.class, () -> commentService.deleteComment(commentId, newUser));
        } catch (Exception e) {
            e.printStackTrace();
        }
        verify(commentRepository, never()).delete(any());
    }


@Test
void testConvertToDTO() {
    LocalDateTime expectedCreatedAt = comment.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);
    LocalDateTime actualCreatedAt = commentDTO.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);

    CommentDTO newComment = commentService.convertToDTO(comment);

    assertNotNull(newComment);
    assertEquals(comment.getId(), newComment.getId());
    assertEquals(comment.getDescription(), newComment.getDescription());
    assertEquals(comment.getTask().getId(), newComment.getTaskId());
    assertEquals(comment.getUser().getId(), newComment.getUserId());
    
    assertEquals(expectedCreatedAt, actualCreatedAt);
}


    @Test
    void testListCommentsByTask_Success() {
        when(commentRepository.findByTaskId(task.getId())).thenReturn(List.of(comment));

        List<CommentDTO> comments = commentService.listCommentsByTask(task.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("This is a comment", comments.get(0).getDescription());
    }

    @Test
    void testListCommentsByTask_NoComments() {
        when(commentRepository.findByTaskId(task.getId())).thenReturn(List.of());

        List<CommentDTO> comments = commentService.listCommentsByTask(task.getId());

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }
}
