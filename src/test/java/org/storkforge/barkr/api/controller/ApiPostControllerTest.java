package org.storkforge.barkr.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.ResponsePost;

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
    @DisplayName("Finds all posts from service")
    void postsReturnsAllPosts() throws Exception {
        ResponseAccount mockAccount = new ResponseAccount(1L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);

        List<ResponsePost> mockPosts = List.of(
                new ResponsePost(1L, "testPost1", mockAccount, LocalDateTime.now()),
                new ResponsePost(2L, "testPost2", mockAccount, LocalDateTime.now())
        );
        when(service.findAll()).thenReturn(mockPosts);

        mvc.perform(get("/api/posts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[0].content").value("testPost1"))
                .andExpect(jsonPath("$.posts[1].content").value("testPost2"));
    }

    @Test
    @DisplayName("Finds a post from service")
    void findAccount() throws Exception {
        ResponseAccount mockAccount = new ResponseAccount(1L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);
        ResponsePost mockPosts = new ResponsePost(1L, "testPost1", mockAccount, LocalDateTime.now());

        when(service.findById(1L)).thenReturn(mockPosts);

        mvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("testPost1"))
                .andExpect(jsonPath("$.account.username").value("testAccount"));
    }
}

