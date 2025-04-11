package org.storkforge.barkr.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MistralAiController {
    private static final Logger logger = LoggerFactory.getLogger(MistralAiController.class);
    String genKey = "generation";

    @Value("${ai.mistral.joke-prompt}")
    private String jokePrompt;

    private final MistralAiChatModel chatModel;

    @Autowired
    public MistralAiController(MistralAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String, String> generate() {
        try {
            String response = this.chatModel.call(jokePrompt);
            return Map.of(genKey, response);

        } catch (org.springframework.ai.retry.NonTransientAiException e) {
            if (e.getMessage().contains("401")) {
                logger.error("Unauthorized access: {}", e.getMessage(), e);
                return Map.of(genKey, "Unauthorized access. Please verify your API key.");
            }
            if (e.getMessage().contains("500")) {
                logger.error("AI server is down: {}", e.getMessage(), e);
                return Map.of(genKey, "The AI server is currently unavailable. Please try again later.");
            }
            logger.error("Non-transient AI exception: {}", e.getMessage(), e);
            return Map.of(genKey, "An error occurred. Please try again later.");

        } catch (Exception e) {
            logger.error("Unexpected error generating AI response: {}", e.getMessage(), e);
            return Map.of(genKey, "Sorry, I couldn't generate a response. Please try again later.");
        }
    }
}