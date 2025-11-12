package com.genAiI.JavaAI.Model;

public class PromptHistoryItem {
    private final String prompt;
    private final String response;

    public PromptHistoryItem(String prompt, String response) {
        this.prompt = prompt;
        this.response = response;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getResponse() {
        return response;
    }
}
