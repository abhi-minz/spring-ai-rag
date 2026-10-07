package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

/**
 * Splits long text into smaller overlapping chunks using Spring AI's
 * {@link TokenTextSplitter}. Chunk size is configurable per request.
 */
@Service
public class ChunkingService {
    /**
     * Splits text into chunks with configurable size.
     *
     * @param text              the raw text to split
     * @param metadata          metadata to attach to every chunk
     * @param chunkSize         target tokens per chunk
     * @param minChunkSizeChars minimum characters a chunk must have
     * @param overlap           reserved for future use
     * @return the list of chunk documents
     */
    public List<Document> split(String text, Map<String, Object> metadata, int chunkSize, int minChunkSize,
            int overlap) {
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(chunkSize)
                .withMinChunkSizeChars(minChunkSize).withMinChunkLengthToEmbed(5).withMaxNumChunks(10000)
                .withKeepSeparator(true).build();

        Document whole = new Document(text, metadata);
        return splitter.apply(List.of(whole));
    }

    /**
     * Convenience overload with sensible defaults (800-token chunks).
     */
    public List<Document> split(String text, Map<String, Object> metadata) {
        return split(text, metadata, 800, 350, 0);
    }

    public List<Document> splitWithPageMetadata(List<Document> pages, String source, int chunkSize) {
        List<Document> result = new ArrayList<>();
        for (Document page : pages) {
            Object pageNum = page.getMetadata().get("page_number");
            List<Document> pageChunks = split(page.getText(),
                    Map.of("source", source, "page_number", pageNum == null ? "?" : pageNum), chunkSize, 350, 0);

            for (int i = 0; i < pageChunks.size(); i++) {
                Document c = pageChunks.get(i);
                c.getMetadata().put("chunk_index", i);
                c.getMetadata().put("total_chunks", pageChunks.size());
                result.add(c);
            }
        }
        return result;
    }
}
