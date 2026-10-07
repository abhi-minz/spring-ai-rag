package com.personal.RetrievalAugmentedGeneration.Controller;

import com.personal.RetrievalAugmentedGeneration.Service.ChatService;
import com.personal.RetrievalAugmentedGeneration.Service.ChunkingService;
import com.personal.RetrievalAugmentedGeneration.Service.DocumentIngestionService;
import com.personal.RetrievalAugmentedGeneration.Service.PdfReaderService;
import com.personal.RetrievalAugmentedGeneration.Service.VectorStoreService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    private final DocumentIngestionService documentIngestionService;
    private final ChatService chatService;
    private final VectorStoreService vectorStoreService;
    private final ChunkingService chunkingService;
    private final PdfReaderService pdfReaderService;

    public ChatController(ChatService chatService, VectorStoreService vectorStoreService,
            ChunkingService chunkingService, PdfReaderService pdfReaderService,
            DocumentIngestionService documentIngestionService) {
        this.chatService = chatService;
        this.vectorStoreService = vectorStoreService;
        this.chunkingService = chunkingService;
        this.pdfReaderService = pdfReaderService;
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/ask")
    public String postAsk(@RequestBody String question) {
        return chatService.chat(question);
    }

    /**
     * Inserts a single pre-chunked piece of text.
     *
     * Use /api/ingest-document instead if you have raw text that needs chunking.
     * This endpoint is for testing and cases where you already know the chunk
     * boundaries.
     */

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

    @GetMapping("/pdf/preview")
    public Map<String, Object> previewPdf(@RequestParam String path) throws Exception {
        List<org.springframework.ai.document.Document> docs = pdfReaderService.readPdf(path);
        return Map.of(
                "path", path, "pages", docs.size(), "firstPagePreview",
                docs.isEmpty() ? "" : docs.get(0).getText().substring(0, Math.min(300, docs.get(0).getText().length())),
                "firstPageMetaData", docs.isEmpty() ? Map.of() : docs.get(0).getMetadata());
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return documentIngestionService.ingestPdf(file);
    }

    @GetMapping("/read-pdf")
    public Map<String, Object> readPdf(@RequestParam String path) {
        List<Document> docs = pdfReaderService.readPdf(path);
        return Map.of("path", path, "pages", docs.size(), "preview",
                docs.stream().map(d -> Map.of("page", d.getMetadata().getOrDefault("page_number", "?"), "chars",
                        d.getText().length(), "text", d.getText().substring(0, Math.min(200, d.getText().length()))))
                        .toList());
    }

}
