package org.storkforge.barkr.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.exceptions.PostNotFound;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiPostController.class)
class ApiPostControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PostService service;

    @Test
    @WithMockUser
    @DisplayName("Finds all posts from service")
    void postsReturnsAllPosts() throws Exception {
        ResponseAccount mockAccount = new ResponseAccount(1L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);

        List<ResponsePost> mockPosts = List.of(
                new ResponsePost(1L, "testPost1", mockAccount, LocalDateTime.now()),
                new ResponsePost(2L, "testPost2", mockAccount, LocalDateTime.now())
        );
        Page<ResponsePost> postPage = new PageImpl<>(mockPosts);

        Pageable pageable = PageRequest.of(0, 10);
        when(service.findAll(pageable)).thenReturn(postPage);

        mvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].content").value("testPost1"))
                .andExpect(jsonPath("$.content[1].content").value("testPost2"));
    }

    @Test
    @WithMockUser
    @DisplayName("Finds a post from service")
    void findPost() throws Exception {
        ResponseAccount mockAccount = new ResponseAccount(1L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);
        ResponsePost mockPost = new ResponsePost(1L, "testPost1", mockAccount, LocalDateTime.now());

        when(service.findById(1L)).thenReturn(mockPost);

        mvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("testPost1"))
                .andExpect(jsonPath("$.account.username").value("testAccount"));
    }


    @Test
    @WithMockUser
    @DisplayName("Handles error for nonexistent id")
    void handlesErrorForNonexistentId() throws Exception {
        when(service.findById(1L)).thenThrow(new PostNotFound("Post with id: 1 was not found"));

        mvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
