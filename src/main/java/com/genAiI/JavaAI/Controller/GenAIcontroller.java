package com.genAiI.JavaAI.Controller;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.genAiI.JavaAI.Service.GenAIservice;
import com.genAiI.JavaAI.Service.ImageAIService;

@Controller
public class GenAIcontroller {

    private final GenAIservice textService;
    private final ImageAIService imageService;

    public GenAIcontroller(GenAIservice textService, ImageAIService imageService) {
        this.textService = textService;
        this.imageService = imageService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("history", textService.getPromptHistory());
        return "home";
    }

    @PostMapping("/chat")
    public String handleChat(@RequestParam("prompt") String prompt, Model model) {
        try {
            String rawResponse = textService.chatTemplate(prompt);
            String cleanResponse = formatStructuredResponse(rawResponse);

            model.addAttribute("response", cleanResponse);
            model.addAttribute("prompt", prompt);
            model.addAttribute("history", textService.getPromptHistory());
            return "home";
        } catch (Exception e) {
            model.addAttribute("error", "⚠️ Error while processing text: " + e.getMessage());
            return "home";
        }
    }

    @PostMapping("/image")
    public String handleImageUpload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("prompt") String prompt,
                                    Model model) throws IOException {
        try {
            byte[] fileBytes = file.getBytes();
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = MimeTypeUtils.IMAGE_JPEG_VALUE;
            }

            MimeType finalMimeType = MimeTypeUtils.parseMimeType(contentType);

            String rawResponse = imageService.processImagePrompt(prompt, resource, finalMimeType);
            String cleanResponse = formatStructuredResponse(rawResponse);

            model.addAttribute("response", cleanResponse);
            model.addAttribute("prompt", prompt);
            model.addAttribute("history", textService.getPromptHistory());
            return "home";
        } catch (IOException | IllegalArgumentException e) {
            model.addAttribute("error", "⚠️ Error processing image: " + e.getMessage());
            return "home";
        }
    }

    @GetMapping("/history")
    public String promptHistory(Model model) {
        model.addAttribute("history", textService.getPromptHistory());
        return "promptHistory";
    }

    /** ✅ Cleans AI output */
    private String formatStructuredResponse(String raw) {
        if (raw == null || raw.isBlank()) return "No response received.";

        String formatted = raw
                .replaceAll("<[^>]*>", "")
                .replaceAll("(?m)^#{1,6}\\s*", "\n\n")
                .replaceAll("(?m)^\\d+\\.\\s*", "\n• ")
                .replaceAll("(?m)^[-*]\\s*", "\n• ")
                .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
                .replaceAll("\\*(.*?)\\*", "$1")
                .replaceAll("_([^_]+)_", "$1")
                .replaceAll("\\|+", "")
                .replaceAll(":+", "")
                .replaceAll("(?m)^---$", "\n------------------------------\n")
                .replaceAll("\\r", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();

        return formatted.replaceAll("(?m)([^\\n])\\n([^\\n])", "$1\n$2");
    }
}
