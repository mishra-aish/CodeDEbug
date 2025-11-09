package com.genAiI.JavaAI.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class GenAIservice {

    private final ChatClient chatClient;
    private final List<String> promptHistory = new ArrayList<>();

    public GenAIservice(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    // Handles text-only prompts
    public String chatTemplate(String prompts) {
        String response = this.chatClient
                .prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user(user -> user.text(prompts))
                .call()
                .content();

        promptHistory.add(prompts);
        return response;
    }

    public List<String> getPromptHistory() {
        return new ArrayList<>(promptHistory);
    }
}
