package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

/**
 * Retrieval-Augmented Generation service.
 * Retrieves relevant chunks from the vector store, builds a grounded
 * prompt, and calls the LLM to produce an answer.
 */
@Service
public class RagService {

    private static final double MAX_DISTANCE = 0.55;

    private final VectorStoreService vectorStoreService;
    private final ChatClient chatClient;

    public RagService(VectorStoreService vectorStoreService, ChatClient.Builder chatClientBuilder) {
        this.vectorStoreService = vectorStoreService;
        this.chatClient = chatClientBuilder.build();
    }

    public Map<String, Object> ask(String question, int topK) {

        List<Document> candidates = vectorStoreService.search(question, topK);

        List<Document> relevant = candidates.stream().filter(d -> {
            Object dist = d.getMetadata().get("distance");
            return dist != null && ((Number) dist).doubleValue() < MAX_DISTANCE;
        }).toList();

        if (relevant.isEmpty()) {
            return Map.of("question", question, "answer", "I don't know based on the provided documents.", "sources",
                    List.<SourceCard>of(), "topK", topK, "filtered", candidates.size());
        }

        String context = relevant.stream()
                .map(d -> String.format("[page %s] %s", d.getMetadata().getOrDefault("page_number", "?"), d.getText()))
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = """
                You are a research assistant answering questions strictly from the provided context.

                Rules:
                1. Answer ONLY using the context below. Do not use outside knowledge.
                2. Every factual claim MUST include a citation in the format [page N].
                3. If the context does not contain the answer, respond exactly with:
                   "I don't know based on the provided documents."
                4. If different pages give conflicting information, point out the conflict.
                5. Be concise but complete. Prefer bullet points for multi-part answers.

                Context:
                %s

                Question: %s

                Answer (with [page N] citations):
                """.formatted(context, question);

        String answer = chatClient.prompt().user(prompt).call().content();

        List<SourceCard> sources = relevant.stream()
                .map(d -> {
                    Object page = d.getMetadata().getOrDefault("page_number", "?");
                    Object source = d.getMetadata().getOrDefault("source", "unknown");
                    Object chunkIdx = d.getMetadata().getOrDefault("chunk_index", "?");
                    Object totalChunks = d.getMetadata().getOrDefault("total_chunks", "?");

                    Object rawDist = d.getMetadata().get("distance");
                    double dist = rawDist == null ? -1.0 : ((Number) rawDist).doubleValue();

                    String fullText = d.getText();
                    String preview = fullText.substring(0, Math.min(200, fullText.length()));

                    return new SourceCard(d.getId(), source.toString(), page, chunkIdx, totalChunks, dist, fullText,
                            preview, String.format("[%s, page %s]", source, page));
                })
                .toList();

        return Map.of("question", question, "answer", answer, "sources", sources, "topK", topK, "filtered",
                candidates.size() - relevant.size());
    }

}
