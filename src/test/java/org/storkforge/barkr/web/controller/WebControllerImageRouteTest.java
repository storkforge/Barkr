package org.storkforge.barkr.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.DogFactService;
import org.storkforge.barkr.domain.PostService;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerImageRouteTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private DogFactService dogFactService;

    @Test
    @DisplayName("Returns account image if present")
    void returnsAccountImageIfPresent() throws Exception {
        byte[] image = "test-image".getBytes();
        when(accountService.getAccountImage(1L)).thenReturn(image);

        mockMvc.perform(get("/account/1/image"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(content().bytes(image));
    }

    @Test
    @DisplayName("Returns fallback image if account image is null")
    void returnsFallbackImageIfNull() throws Exception {
        byte[] fallback;
        try (InputStream is = new ClassPathResource("static/images/logo/BarkrNoText.png").getInputStream()) {
            fallback = is.readAllBytes();
        }

        when(accountService.getAccountImage(1L)).thenReturn(fallback);

        mockMvc.perform(get("/account/1/image"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(content().bytes(fallback));
    }

    @Test
    @DisplayName("Redirects with error if file is empty")
    void fileIsEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "image.png", "image/png", new byte[0]);

        mockMvc.perform(multipart("/account/1/upload").file(emptyFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "File is empty"));
    }

    @Test
    @DisplayName("Redirects with error if file is not an image")
    void fileIsNotImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "some-text".getBytes());

        mockMvc.perform(multipart("/account/1/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "File is not an image"));
    }

    @Test
    @DisplayName("Redirects with error if file is too large")
    void fileIsTooLarge() throws Exception {
        byte[] large = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile("file", "big.png", "image/png", large);

        mockMvc.perform(multipart("/account/1/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "File is too large"));
    }

    @Test
    @DisplayName("Redirects to root after successful image upload")
    void uploadSuccess() throws Exception {
        byte[] img = "image-content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "pic.png", "image/png", img);

        doNothing().when(accountService).updateImage(1L, img);

        mockMvc.perform(multipart("/account/1/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
