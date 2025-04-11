package org.storkforge.barkr.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.exceptions.InvalidApiKey;
import org.storkforge.barkr.exceptions.ServerUnavailable;

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
        } catch (InvalidApiKey e) {
            logger.error("Invalid API Key: {}", e.getMessage(), e);
            return Map.of(genKey, "Invalid API key. Please check your configuration.");
        } catch (ServerUnavailable e) {
            logger.error("AI server is down: {}", e.getMessage(), e);
            return Map.of(genKey, "The AI server is currently unavailable. Please try again later.");
        }catch (Exception e) {
            logger.error("Unexpected error generating AI response: {}", e.getMessage(), e);
            return Map.of(genKey, "Sorry, I couldn't generate a response. Please try again later.");
        }
    }


}
