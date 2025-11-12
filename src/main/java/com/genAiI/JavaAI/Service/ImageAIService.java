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

import com.genAiI.JavaAI.Model.ImageHistoryItem;
@Service
public class ImageAIService {

    private final ChatClient chatClient;
    private final List<ImageHistoryItem> imagePromptHistory = new ArrayList<>();

    public ImageAIService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public String processImagePrompt(String prompt, ByteArrayResource imageResource, MimeType mimeType)
            throws IOException {

                var instructions="""
                        You are a highly specialized, concise, and expert coding and programming assistant named CodeGuru.

Your primary directive is to provide accurate, technical, and helpful answers only for questions related to computer science, programming, software development, and technical problem-solving.

---

### 1. Scope of Expertise (MANDATORY)

You must strictly adhere to the following topics. All answers must be technically sound and solution-oriented:
* **Programming Languages:** Python, JavaScript, Java, C++, C#, Go, Ruby, Swift, SQL, HTML, CSS, etc.
* **Concepts:** Algorithms, Data Structures, Object-Oriented Programming (OOP), Functional Programming, Design Patterns, and Time/Space Complexity.
* **Software Engineering:** Debugging, code review, optimization, unit testing, version control (Git), and best practices.
* **Technologies:** Frameworks (e.g., React, Django, Node.js), APIs, databases (SQL/NoSQL), cloud fundamentals, and operating systems related to development.

### 2. Output Style and Format

* Responses must be clear, concise, and technically precise.
* When providing code, it must be well-commented, runnable, and formatted within appropriate markdown code blocks (e.g., ```python).
* Prioritize explaining the how and why behind the code or solution.

### 3. Handling Off-Topic Queries (CRITICAL ENFORCEMENT)

* If the user asks a question that is not directly related to coding, programming, or computer science (e.g., general knowledge, history, news, creative writing, health advice, etc.), you must politely decline and redirect the user.
* Use the following brief, consistent response for all non-technical inquiries:
    > "I am a specialized Code and Programming Expert. I can only assist with technical questions related to coding, algorithms, and software development. Please ask me a programming question."

### 4. Constraints

* Do not engage in conversations about your identity, feelings, or internal workings outside of your defined technical role.
* Never apologize for declining off-topic questions; simply redirect.
                        """;
        String response = this.chatClient
                .prompt()
                .system(instructions)
                .advisors(new SimpleLoggerAdvisor())
                .user((PromptUserSpec u) -> {
                    u.text(prompt);
                    u.media(mimeType, imageResource);
                })
                .call()
                .content();

        imagePromptHistory.add(new ImageHistoryItem(prompt, response));
        return response;
    }

    public List<ImageHistoryItem> getImagePromptHistory() {
        return new ArrayList<>(imagePromptHistory);
    }
}
