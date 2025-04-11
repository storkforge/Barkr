package org.storkforge.barkr.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.exceptions.InvalidApiKey;
import org.storkforge.barkr.exceptions.ServerUnavailable;

import java.util.Map;

@RestController
public class MistralAiController {
    private static final Logger logger = LoggerFactory.getLogger(MistralAiController.class);
    String gen = "generation";

    private final MistralAiChatModel chatModel;

    @Autowired
    public MistralAiController(MistralAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String, String> generate() {
        try {
            String response = this.chatModel.call("tell me a dog joke like you are a dog and dont write anything else and dont explain the joke");
            return Map.of(gen, response);
        } catch (InvalidApiKey e) {
            logger.error("Invalid API Key: {}", e.getMessage(), e);
            return Map.of(gen, "Invalid API key. Please check your configuration.");
        } catch (ServerUnavailable e) {
            logger.error("AI server is down: {}", e.getMessage(), e);
            return Map.of(gen, "The AI server is currently unavailable. Please try again later.");
        }catch (Exception e) {
            logger.error("Unexpected error generating AI response: {}", e.getMessage(), e);
            return Map.of(gen, "Sorry, I couldn't generate a response. Please try again later.");
        }
    }


}
