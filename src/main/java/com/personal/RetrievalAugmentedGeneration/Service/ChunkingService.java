package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {

    public List<Document> split(String text, Map<String, Object> metadata, int chunkSize, int minChunkSize,
            int overlap) {
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(chunkSize)
                .withMinChunkSizeChars(minChunkSize).withMinChunkLengthToEmbed(5).withMaxNumChunks(10000)
                .withKeepSeparator(true).build();

        Document whole = new Document(text, metadata);
        return splitter.apply(List.of(whole));
    }

    public List<Document> split(String text, Map<String, Object> metadata) {
        return split(text, metadata, 800, 350, 0);
    }
}
