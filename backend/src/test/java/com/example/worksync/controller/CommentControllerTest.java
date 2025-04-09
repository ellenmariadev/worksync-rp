package com.example.worksync.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.worksync.dto.requests.CommentDTO;
import com.example.worksync.model.User;
import com.example.worksync.service.CommentService;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    private User testUser;
    private CommentDTO testComment;
    private List<CommentDTO> testComments;

    @BeforeEach
    void setUp() {
        CommentController commentController = new CommentController(commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        testComment = new CommentDTO(1L, "Test comment", 1L, 1L, null);
        CommentDTO anotherComment = new CommentDTO(2L, "Another comment", 1L, 1L, null);

        testComments = Arrays.asList(testComment, anotherComment);
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("List comments by task should return a list of comments")
    void listCommentsByTask_ShouldReturnComments() throws Exception {
        when(commentService.listCommentsByTask(1L)).thenReturn(testComments);

        mockMvc.perform(get("/comments/task/1")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].description").value("Test comment"))
                .andExpect(jsonPath("$[1].description").value("Another comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Add comment should return created comment")
    void addComment_ShouldReturnCreatedComment() throws Exception {
        when(commentService.addComment(any(CommentDTO.class), any(User.class))).thenReturn(testComment);

        mockMvc.perform(post("/comments")
                .contentType("application/json")
                .content("{\"description\":\"Test comment\", \"taskId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Delete comment should return no content")
    void deleteComment_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/comments/1")
                .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
                .andExpect(status().isNoContent());
    }
}
