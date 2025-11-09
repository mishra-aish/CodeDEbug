package com.genAiI.JavaAI.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.PromptUserSpec;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

@Service
public class ImageAIService {

    private final ChatClient chatClient;
    private final List<String> imagePromptHistory = new ArrayList<>();

    public ImageAIService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public String processImagePrompt(String prompt, ByteArrayResource imageResource, MimeType mimeType)
            throws IOException {

        String response = this.chatClient
                .prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user((PromptUserSpec u) -> {
                    u.text(prompt);
                    u.media(mimeType, imageResource);
                })
                .call()
                .content();

        imagePromptHistory.add(prompt);
        return response;
    }

    public List<String> getImagePromptHistory() {
        return new ArrayList<>(imagePromptHistory);
    }
}
