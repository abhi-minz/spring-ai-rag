package com.personal.RetrievalAugmentedGeneration.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Orchestrates the full ingestion pipeline:
 * upload -> save -> read PDF -> chunk -> embed -> store.
 */
@Service
public class DocumentIngestionService {

    private final PdfReaderService pdfReaderService;
    private final ChunkingService chunkingService;
    private final VectorStoreService vectorStoreService;

    public DocumentIngestionService(PdfReaderService pdfReaderService, ChunkingService chunkingService,
            VectorStoreService vectorStoreService) {
        this.pdfReaderService = pdfReaderService;
        this.chunkingService = chunkingService;
        this.vectorStoreService = vectorStoreService;
    }

    /**
     * Ingests an uploaded PDF file into the vector store.
     *
     * @param file the uploaded PDF
     * @return summary containing file name, page count, total chunks, and chunk IDs
     */
    public Map<String, Object> ingestPdf(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        Path tempPath = Files.createTempFile("upload-", "-" + (fileName != null ? fileName : "doc.pdf"));
        file.transferTo(tempPath.toFile());
        List<Document> pages = pdfReaderService.readPdf(tempPath.toString());

        List<Document> allChunks = new ArrayList<>();
        for (Document page : pages) {
            Object pageNum = page.getMetadata().get("page_number");

            Map<String, Object> chunkMeta = new HashMap<>();
            chunkMeta.put("source", fileName);
            chunkMeta.put("page_number", pageNum);
            chunkMeta.put("file_type", "pdf");

            List<Document> chunks = chunkingService.split(page.getText(), chunkMeta);
            allChunks.addAll(chunks);
        }
        List<String> ids = vectorStoreService.addAll(allChunks);
        Files.deleteIfExists(tempPath);
        Map<String, Object> result = new HashMap<>();
        result.put("file", fileName);
        result.put("pages", pages.size());
        result.put("totalChunks", allChunks.size());
        result.put("chunkIds", ids);
        return result;
    }

}
