package com.personal.RetrievalAugmentedGeneration.Controller;

import com.personal.RetrievalAugmentedGeneration.Service.ChatService;
import com.personal.RetrievalAugmentedGeneration.Service.ChunkingService;
import com.personal.RetrievalAugmentedGeneration.Service.VectorStoreService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final VectorStoreService vectorStoreService;
    private final ChunkingService chunkingService;

    public ChatController(ChatService chatService, VectorStoreService vectorStoreService,
            ChunkingService chunkingService) {
        this.chatService = chatService;
        this.vectorStoreService = vectorStoreService;
        this.chunkingService = chunkingService;
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

    @PostMapping("ingest-document")
    public Map<String, Object> ingestDocument(@RequestBody Map<String, Object> body) {
        String text = (String) body.get("text");
        String source = (String) body.getOrDefault("source", "unknown");

        int chunkSize = body.containsKey("chunkSize") ? ((Number) body.get("chunkSize")).intValue() : 800;
        int minChars = body.containsKey("minChars") ? ((Number) body.get("minChars")).intValue() : 350;

        List<Document> chunks = chunkingService.split(text, Map.of("source", source), chunkSize, minChars, 0);
        List<String> ids = vectorStoreService.addAll(chunks);

        return Map.of("source", source, "chunks", chunks.size(), "ids", ids);
    }

    @GetMapping("search")
    public List<Map<String, Object>> search(@RequestParam String q, @RequestParam(defaultValue = "3") int k) {
        return vectorStoreService.search(q, k).stream()
                .map(doc -> Map.of("id", doc.getId(), "content", doc.getText(), "metadata", doc.getMetadata()))
                .toList();
    }

}
