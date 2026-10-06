package com.personal.RetrievalAugmentedGeneration.Controller;

import com.personal.RetrievalAugmentedGeneration.Service.ChatService;
import com.personal.RetrievalAugmentedGeneration.Service.VectorStoreService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final VectorStoreService vectorStoreService;

    public ChatController(ChatService chatService, VectorStoreService vectorStoreService) {
        this.chatService = chatService;
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/ask")
    public String postAsk(@RequestBody String question) {
        return chatService.chat(question);
    }

    @PostMapping("ingest")
    public String ingest(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        String source = body.getOrDefault("source", "manual");
        String id = vectorStoreService.add(content, Map.of("source", source));
        return "Inserted : " + id;
    }

    @GetMapping("search")
    public List<Map<String, Object>> search(@RequestParam String q, @RequestParam(defaultValue = "3") int k) {
        return vectorStoreService.search(q, k).stream()
                .map(doc -> Map.of("id", doc.getId(), "content", doc.getText(), "metadata", doc.getMetadata()))
                .toList();
    }

}
