package com.genAiI.JavaAI.Model;

public class ImageHistoryItem {
    private final String prompt;
    private final String response;

    public ImageHistoryItem(String prompt, String response) {
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
