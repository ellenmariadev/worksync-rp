package com.example.worksync.service;

import com.example.worksync.dto.requests.CommentDTO;
import com.example.worksync.exceptions.NotFoundException;
import com.example.worksync.exceptions.ResourceNotFoundException;
import com.example.worksync.exceptions.UnauthorizedAccessException;
import com.example.worksync.model.Comment;
import com.example.worksync.model.Task;
import com.example.worksync.model.User;
import com.example.worksync.repository.CommentRepository;
import com.example.worksync.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

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
void testDeleteComment() {
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

    try {
        commentService.deleteComment(comment.getId(), user);
        fail("Expected ResourceNotFoundException to be thrown");
    } catch (ResourceNotFoundException e) {
        // Test passed, exception is expected.
    }
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
