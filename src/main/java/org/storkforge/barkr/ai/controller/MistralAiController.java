package org.storkforge.barkr.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.roles.BarkrRole;

import java.util.Map;

@RestController
public class MistralAiController {
    private static final Logger logger = LoggerFactory.getLogger(MistralAiController.class);
    String genKey = "generation";

    @Value("${ai.mistral.joke-prompt}")
    private String jokePrompt;

    private final MistralAiChatModel chatModel;

    public MistralAiController(MistralAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PreAuthorize("hasRole('PREMIUM')")
    @GetMapping("/ai/generate")
    public ResponseEntity<Map<String, String>> generate() {
        try {
            String response = this.chatModel.call(jokePrompt);
            return ResponseEntity.ok(Map.of(genKey, response));

        } catch (Exception e) {
            logger.error("Unexpected error generating AI response: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(genKey, "Sorry, I couldn't generate a response. Please try again later."));
        }
    }
}
