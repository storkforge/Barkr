package org.storkforge.barkr.api.controller;

import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MistralAiController {

    private final MistralAiChatModel chatModel;

    @Autowired
    public MistralAiController(MistralAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "tell me a dog joke like you are a dog and dont write anything else and dont explain the joke") String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return Map.of("generation", "Please provide a valid message.");
            }

            String response = this.chatModel.call(message);
            return Map.of("generation", response);
        } catch (Exception e) {
            System.err.println("Error generating AI response: " + e.getMessage());
            return Map.of("generation", "Sorry, I couldn't generate a response. Please try again later.");
        }
    }


}
