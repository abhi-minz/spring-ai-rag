package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

/**
 * End-to-end PDF ingestion: read → clean → chunk → embed → store.
 */
@Service
public class PdfIngestionService {
    private final PdfReaderService pdfReaderService;
    private final TextCleanupService textCleanupService;
    private final ChunkingService chunkingService;
    private final VectorStoreService vectorStoreService;

    public PdfIngestionService(PdfReaderService pdfReaderService, TextCleanupService textCleanupService,
            ChunkingService chunkingService, VectorStoreService vectorStoreService) {
        this.pdfReaderService = pdfReaderService;
        this.textCleanupService = textCleanupService;
        this.chunkingService = chunkingService;
        this.vectorStoreService = vectorStoreService;
    }

    public Map<String, Object> ingestPdf(String pdfPath, String source, int chunkSize) {
        List<Document> pages = pdfReaderService.readPdf(pdfPath);
        List<Document> cleanedPages = new ArrayList<>();

        for (Document page : pages) {
            String cleaned = textCleanupService.clean(page.getText());
            Document newPage = new Document(cleaned, page.getMetadata());
            cleanedPages.add(newPage);
        }

        List<Document> chunks = chunkingService.splitWithPageMetadata(cleanedPages, source, chunkSize);
        List<String> ids = vectorStoreService.addAll(chunks);

        return Map.of("source", source, "pages", pages.size(), "chunks", chunks.size(), "ids", ids);
    }
}
