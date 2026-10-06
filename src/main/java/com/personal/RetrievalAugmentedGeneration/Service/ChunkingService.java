package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {
    private final TokenTextSplitter splitter = TokenTextSplitter.builder().build();

    public List<Document> split(String text, Map<String, Object> metadata) {
        Document whole = new Document(text, metadata);
        return splitter.apply(List.of(whole));
    }
}
